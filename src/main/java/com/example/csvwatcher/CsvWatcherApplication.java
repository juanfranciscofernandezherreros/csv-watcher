package com.example.csvwatcher;

import com.example.csvwatcher.watcher.WatcherProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(WatcherProperties.class)
public class CsvWatcherApplication {
    public static void main(String[] args) {
        SpringApplication.run(CsvWatcherApplication.class, args);
    }
}
