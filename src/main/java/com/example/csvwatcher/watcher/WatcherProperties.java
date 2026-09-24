package com.example.csvwatcher.watcher;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;
import java.time.Duration;

@ConfigurationProperties("app.watcher")
public record WatcherProperties(Path directory, int stabilityChecks, Duration stabilityInterval) {
    public WatcherProperties {
        if (stabilityChecks < 1) throw new IllegalArgumentException("stabilityChecks must be positive");
        if (stabilityInterval == null || stabilityInterval.isNegative() || stabilityInterval.isZero()) {
            throw new IllegalArgumentException("stabilityInterval must be positive");
        }
    }
}
