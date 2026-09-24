package com.example.csvwatcher.watcher;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FixtureFileClassifierTest {
    private final FixtureFileClassifier classifier = new FixtureFileClassifier();

    @Test
    void acceptsFixtureContract() {
        assertTrue(classifier.supports(Path.of("FIXTURES_europe_euroleague.csv")));
        assertTrue(classifier.supports(Path.of("fixtures_usa_nba.csv")));
        assertEquals("FIXTURES", classifier.classify(Path.of("FIXTURES_europe_euroleague.csv")).orElseThrow());
    }

    @Test
    void classifiesEveryRoutedFileType() {
        assertEquals("RESULTS", classifier.classify(Path.of("RESULTS_20260304090805_spain_acb.csv")).orElseThrow());
        assertEquals("MATCH_SUMMARY", classifier.classify(Path.of("MATCH_SUMMARY_r3iRICYO.csv")).orElseThrow());
        assertEquals("POINT_BY_POINT", classifier.classify(Path.of("POINT_BY_POINT_r3iRICYO.csv")).orElseThrow());
        assertEquals("STATS_PLAYER", classifier.classify(Path.of("player_stats.csv")).orElseThrow());
        assertEquals("SEASONS", classifier.classify(Path.of("SEASONS_spain_acb.csv")).orElseThrow());
    }

    @Test
    void publishesUnknownCsvFilesForRouterValidation() {
        assertTrue(classifier.supports(Path.of("FIXTURES_europe.csv")));
        assertTrue(classifier.supports(Path.of("basketball_matches.csv")));
        assertEquals("UNKNOWN", classifier.classify(Path.of("FIXTURES_europe.csv")).orElseThrow());
        assertEquals("UNKNOWN", classifier.classify(Path.of("basketball_matches.csv")).orElseThrow());
    }

    @Test
    void rejectsNonCsvFiles() {
        assertFalse(classifier.supports(Path.of("basketball_matches.json")));
        assertFalse(classifier.supports(Path.of("README")));
    }
}
