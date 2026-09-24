package com.example.csvwatcher.watcher;

import com.example.csvwatcher.watcher.FileEventKey;
import com.example.csvwatcher.watcher.FileEventValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class FixtureFileWatcher implements SmartLifecycle {
    private static final Logger log = LoggerFactory.getLogger(FixtureFileWatcher.class);

    private final WatcherProperties properties;
    private final FixtureFileClassifier classifier;
    private final FileEventId eventId;
    private final KafkaTemplate<FileEventKey, FileEventValue> kafkaTemplate;
    private final String topic;
    private final Map<Path, String> published = new ConcurrentHashMap<>();
    private volatile boolean running;
    private ExecutorService executor;
    private WatchService watchService;

    public FixtureFileWatcher(WatcherProperties properties, FixtureFileClassifier classifier,
            FileEventId eventId, KafkaTemplate<FileEventKey, FileEventValue> kafkaTemplate,
            @Value("${app.kafka.topic}") String topic) {
        this.properties = properties;
        this.classifier = classifier;
        this.eventId = eventId;
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public synchronized void start() {
        if (running) return;
        Path directory = properties.directory().toAbsolutePath().normalize();
        if (!Files.isDirectory(directory)) {
            throw new IllegalStateException("Watch directory does not exist: " + directory);
        }
        try {
            watchService = FileSystems.getDefault().newWatchService();
            directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to watch directory " + directory, exception);
        }
        running = true;
        executor = Executors.newSingleThreadExecutor(Thread.ofPlatform().name("fixture-file-watcher").factory());
        executor.execute(() -> watch(directory));
        log.info("Watching {} for routable CSV files", directory);
    }

    private void watch(Path directory) {
        scan(directory);
        while (running) {
            try {
                WatchKey key = watchService.take();
                boolean overflow = false;
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                        overflow = true;
                    } else {
                        Path candidate = directory.resolve((Path) event.context()).normalize();
                        publishWhenReady(directory, candidate);
                    }
                }
                if (overflow) scan(directory);
                if (!key.reset()) throw new IllegalStateException("Watch directory is no longer available");
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                return;
            } catch (Exception exception) {
                if (running) log.error("Error while watching CSV files", exception);
            }
        }
    }

    private void scan(Path directory) {
        try (var files = Files.list(directory)) {
            files.filter(Files::isRegularFile).filter(classifier::supports)
                    .sorted().forEach(file -> publishWhenReady(directory, file));
        } catch (IOException exception) {
            log.error("Unable to scan {}", directory, exception);
        }
    }

    private void publishWhenReady(Path directory, Path file) {
        var fileType = classifier.classify(file);
        if (fileType.isEmpty() || !waitUntilStable(file)) return;
        try {
            String uniqueId = eventId.calculate(directory, file);
            Path normalized = file.toAbsolutePath().normalize();
            if (uniqueId.equals(published.get(normalized))) return;

            FileEventKey key = new FileEventKey(uniqueId);
            FileEventValue value = new FileEventValue(fileType.get(), normalized.toString());
            kafkaTemplate.send(topic, key, value).get(30, TimeUnit.SECONDS);
            published.put(normalized, uniqueId);
            log.info("Published {} event {} for {}", fileType.get(), uniqueId, normalized);
        } catch (Exception exception) {
            log.error("Unable to publish CSV event for {}", file, exception);
        }
    }

    private boolean waitUntilStable(Path file) {
        long previousSize = -1;
        long previousModified = -1;
        int stableChecks = 0;
        Duration interval = properties.stabilityInterval();
        while (running && stableChecks < properties.stabilityChecks()) {
            try {
                if (!Files.isRegularFile(file)) return false;
                long size = Files.size(file);
                long modified = Files.getLastModifiedTime(file).toMillis();
                stableChecks = size == previousSize && modified == previousModified ? stableChecks + 1 : 0;
                previousSize = size;
                previousModified = modified;
                if (stableChecks < properties.stabilityChecks()) Thread.sleep(interval.toMillis());
            } catch (IOException exception) {
                return false;
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return running;
    }

    @Override
    public synchronized void stop() {
        running = false;
        try {
            if (watchService != null) watchService.close();
        } catch (IOException exception) {
            log.warn("Unable to close watch service", exception);
        }
        if (executor != null) executor.shutdownNow();
    }

    @Override public boolean isRunning() { return running; }
    @Override public boolean isAutoStartup() { return true; }
    @Override public int getPhase() { return Integer.MAX_VALUE; }
    @Override public void stop(Runnable callback) { stop(); callback.run(); }
}
