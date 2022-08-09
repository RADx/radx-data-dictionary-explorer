package edu.stanford.radx;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-22
 */
public class CollapseTransformer implements CsvTransformer {

    private final String keyColumnName;

    private final Pattern keyColumnPattern;

    private final int keyColumnPatternGroup;


    private final String delimeter;


    private final List<List<String>> currentRun = new ArrayList<>();

    private String currentKey = null;

    public CollapseTransformer(String keyColumnName, Pattern keyColumnPattern, int keyColumnPatternGroup, String delimeter) {
        this.keyColumnName = keyColumnName;
        this.keyColumnPattern = keyColumnPattern;
        this.keyColumnPatternGroup = keyColumnPatternGroup;
        this.delimeter = delimeter;
    }


    @Override
    public List<Csv> transformCsv(Csv csv) {
        var rows = csv.content();
        int keyColumnIndex = csv.getIndex(keyColumnName);
        if(keyColumnIndex == -1) {
            return List.of(csv);
        }
        var mergedContent = new ArrayList<List<String>>();
        rows.forEach(r -> {
            var keyColumnValue = r.get(keyColumnIndex);
            var keyMatcher = keyColumnPattern.matcher(keyColumnValue);
            if(keyMatcher.matches()) {
                var key = keyMatcher.group(keyColumnPatternGroup);
                if(!key.equals(currentKey)) {
                    endRun(csv, mergedContent);
                    currentKey = key;
                }
                currentRun.add(r);
            }
            else {
                // Doesn't match specified pattern but it is still a valid row value
                endRun(csv, mergedContent);
                currentRun.add(r);
                currentKey = keyColumnValue;
            }
        });
        var collapsedCsv = new Csv(csv.coordinates(), csv.header(), mergedContent);
        return List.of(collapsedCsv);
    }


    private void endRun(Csv csv, List<List<String>> mergedContent) {
        int fieldCount = currentRun.stream()
                                   .map(List::size)
                                   .max(Comparator.naturalOrder())
                                   .orElse(0);
        var columns = new ArrayList<Collection<String>>();
        for(int i = 0; i < fieldCount; i++) {
            columns.add(new LinkedHashSet<>());
        }
        currentRun.forEach(r -> {
            for(int i = 0; i < fieldCount; i++) {
                columns.get(i).add(r.get(i));
            }
        });
        var keyColumnIndex = csv.getIndex(keyColumnName);
        var merged = new ArrayList<String>();
        for(int i = 0; i < fieldCount; i++) {
            if (i != keyColumnIndex) {
                var values = columns.get(i);

                    var joined = values
                            .stream()
                            .filter(s -> !s.isBlank())
                            .map(s -> {
                                if(s.contains(delimeter)) {
                                    return "\"" + s + "\"";
                                }
                                else {
                                    return s;
                                }
                            })
                            .collect(Collectors.joining(";"));
                    merged.add(joined);


            }
            else {
                merged.add(currentKey);
            }
        }
        mergedContent.add(merged);
        currentRun.clear();
    }
}
