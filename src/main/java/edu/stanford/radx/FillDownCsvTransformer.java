package edu.stanford.radx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-12
 */
public class FillDownCsvTransformer implements CsvTransformer {

    private final List<String> fieldNames;

    public FillDownCsvTransformer(List<String> fieldNames) {
        this.fieldNames = new ArrayList<>(fieldNames);
    }

    @Override
    public List<Csv> transformCsv(Csv csv) {
        if(fieldNames.isEmpty()) {
            return List.of(csv);
        }
        var replacementIndexes = fieldNames.stream()
                .map(csv::getIndex)
                .filter(index -> index > -1)
                .toList();
        if(replacementIndexes.isEmpty()) {
            return List.of(csv);
        }
        var lastValues = new HashMap<Integer, String>();
        var replacedRows = new ArrayList<List<String>>();
        for (var row :  csv.content()) {
            // Copy of the current row
            var replacementRow = new ArrayList<>(row);
            for (int replacementColumnIndex : replacementIndexes) {
                var columnVal = row.get(replacementColumnIndex);
                if (columnVal.isBlank()) {
                    var lastNonBlankColumnValue = lastValues.getOrDefault(replacementColumnIndex, "");
                    replacementRow.set(replacementColumnIndex, lastNonBlankColumnValue);
                }
                else {
                    // Store for use in subsequent rows
                    lastValues.put(replacementColumnIndex, columnVal);
                }
            }
            replacedRows.add(replacementRow);
        }
        return List.of(new Csv(csv.coordinates(), csv.header(), replacedRows));
    }
}
