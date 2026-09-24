package com.example.csvwatcher.watcher;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class FixtureFileClassifier {
    private static final Pattern CSV = Pattern.compile("^.+\\.csv$", Pattern.CASE_INSENSITIVE);
    private static final String UNKNOWN_FILE_TYPE = "UNKNOWN";
    private static final Map<String, Pattern> FILE_TYPES = new LinkedHashMap<>();

    static {
        FILE_TYPES.put("POINT_BY_POINT", pattern("point_by_point"));
        FILE_TYPES.put("MATCH_SUMMARY", pattern("match_summary"));
        FILE_TYPES.put("STATS_PLAYER", Pattern.compile("^player_stats\\.csv$", Pattern.CASE_INSENSITIVE));
        FILE_TYPES.put("SEASONS", Pattern.compile("^seasons_[^_]+_.+\\.csv$", Pattern.CASE_INSENSITIVE));
        FILE_TYPES.put("RESULTS", Pattern.compile("^results_[^_]+_[^_]+_.+\\.csv$", Pattern.CASE_INSENSITIVE));
        FILE_TYPES.put("FIXTURES", Pattern.compile("^fixtures_[^_]+_.+\\.csv$", Pattern.CASE_INSENSITIVE));
    }

    public boolean supports(Path path) {
        return classify(path).isPresent();
    }

    public Optional<String> classify(Path path) {
        if (path == null || path.getFileName() == null) return Optional.empty();
        String fileName = path.getFileName().toString();
        if (!CSV.matcher(fileName).matches()) return Optional.empty();
        return Optional.of(FILE_TYPES.entrySet().stream()
                .filter(entry -> entry.getValue().matcher(fileName).matches())
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(UNKNOWN_FILE_TYPE));
    }

    private static Pattern pattern(String prefix) {
        return Pattern.compile("^" + prefix + "(?:_.+)?\\.csv$", Pattern.CASE_INSENSITIVE);
    }
}
