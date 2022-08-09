package edu.stanford.radx;

import java.util.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-25
 */
public class RetainFieldsTransformer implements CsvTransformer {

    private final Set<String> fieldNames = new HashSet<>();

    public RetainFieldsTransformer(Collection<String> fieldNamesToRetain) {
        fieldNames.addAll(fieldNamesToRetain);
    }

    @Override
    public List<Csv> transformCsv(Csv csv) {
        var header = csv.header();
        var indexesToRetain = new ArrayList<Integer>();
        for(int i = 0; i < header.size(); i++) {
            var h = header.get(i);
            if(fieldNames.contains(h)) {
                indexesToRetain.add(i);
            }
        }
        if(indexesToRetain.isEmpty()) {
            return List.of(new Csv(csv.coordinates(), List.of(), List.of()));
        }
        var retainedHeader = new ArrayList<String>();
        for(var i : indexesToRetain) {
            retainedHeader.add(header.get(i));
        }
        var retainedContent = csv.content().stream()
                                 .map(row -> retainIndexes(indexesToRetain, row)).toList();
        return List.of(new Csv(csv.coordinates(), retainedHeader, retainedContent));
    }

    private List<String> retainIndexes(ArrayList<Integer> indexesToRetain, List<String> row) {
        var retainedRow = new ArrayList<String>();
        for (var retainedIndex : indexesToRetain) {
            if (retainedIndex < row.size()) {
                retainedRow.add(row.get(retainedIndex));
            }
            else {
                retainedRow.add("");
            }
        }
        return retainedRow;
    }
}
