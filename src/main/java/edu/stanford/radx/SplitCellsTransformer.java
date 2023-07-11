package edu.stanford.radx;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-12-01
 */
public class SplitCellsTransformer implements CsvTransformer {

    private final String fieldName;

    private final String delimiters;

    private final RowFilter rowFilter;

    public SplitCellsTransformer(String fieldName, String delimiters, RowFilter rowFilter) {
        this.fieldName = fieldName;
        this.delimiters = delimiters;
        this.rowFilter = rowFilter;
    }

    @Override
    public List<Csv> transformCsv(Csv csv) {
        var columnIndex = csv.getIndex(fieldName);
        if(columnIndex == -1) {
            return List.of(csv);
        }
        var resultRows = new ArrayList<List<String>>();
        csv.content()
                .forEach(row -> {
                    if (columnIndex < row.size() && rowFilter.isMatched(csv, row)) {
                            var fieldValue = row.get(columnIndex);
                            var splitCharacterClass = "[" + delimiters + "]";
                            Stream.of(fieldValue.split(splitCharacterClass))
                                    .forEach(splitValue -> {
                                        List<String> splitRow = new ArrayList<>();
                                        for(int i = 0; i < row.size(); i++) {
                                            if(i == columnIndex) {
                                                splitRow.add(splitValue.trim());
                                            }
                                            else {
                                                splitRow.add(row.get(i));
                                            }
                                        }
                                        resultRows.add(splitRow);
                                    });
                    }
                    else {
                        resultRows.add(row);
                    }
                });
        return List.of(new Csv(csv.coordinates(), csv.header(), resultRows));
    }
}
