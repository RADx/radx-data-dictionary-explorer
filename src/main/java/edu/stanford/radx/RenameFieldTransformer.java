package edu.stanford.radx;

import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-12-07
 */
public class RenameFieldTransformer implements CsvTransformer {

    private final String fieldName;

    private final String newName;

    public RenameFieldTransformer(String fieldName, String newName) {
        this.fieldName = fieldName;
        this.newName = newName;
    }

    @Override
    public List<Csv> transformCsv(Csv csv) {
        var newHeader = csv.header().stream().map(this::renameField).toList();
        return List.of(new Csv(csv.coordinates(), newHeader, csv.content()));

    }

    private String renameField(String h) {
        if (h.trim().equals(fieldName)) {
            return newName;
        }
        else {
            return h;
        }
    }
}
