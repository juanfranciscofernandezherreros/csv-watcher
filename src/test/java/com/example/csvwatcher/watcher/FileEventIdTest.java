package com.example.csvwatcher.watcher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class FileEventIdTest {
    @TempDir Path directory;

    @Test
    void isStableAndChangesWithContent() throws Exception {
        Path file = directory.resolve("FIXTURES_europe_euroleague.csv");
        Files.writeString(file, "a,b,c,d\n1,2,3,4\n");
        FileEventId ids = new FileEventId();

        String first = ids.calculate(directory, file);
        assertEquals(first, ids.calculate(directory, file));

        Files.writeString(file, "a,b,c,d\n5,6,7,8\n");
        assertNotEquals(first, ids.calculate(directory, file));
    }
}
