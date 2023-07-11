package edu.stanford.radx;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-11-28
 */
public class ReplaceTransformer implements CsvTransformer {

    private final RowFilter rowFilter;

    private final Pattern findPattern;

    private final String replaceString;

    public ReplaceTransformer(RowFilter rowFilter, Pattern findPattern, String replaceString) {
        this.rowFilter = rowFilter;
        this.findPattern = findPattern;
        this.replaceString = replaceString;
    }

    @Override
    public List<Csv> transformCsv(Csv csv) {
        var transformedRows = csv.content().stream()
                                 .map(row -> replaceCellsInRow(csv, row))
                                 .toList();
        return List.of(new Csv(csv.coordinates(), csv.header(), transformedRows));
    }

    private List<String> replaceCellsInRow(Csv csv, List<String> row) {
        if(!rowFilter.isMatched(csv, row)) {
            return row;
        }
        return row.stream().map(this::replaceStringInCell).toList();
    }

    private String replaceStringInCell(String cell) {
        var matcher = findPattern.matcher(cell);
        if (matcher.find()) {
            return matcher.replaceAll(replaceString);
        }
        else {
            return cell;
        }
    }
}
