package edu.stanford.radx.cli;

import edu.stanford.radx.Csv;
import edu.stanford.radx.CsvTransformer;
import edu.stanford.radx.GlobalCodeBookFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-07-11
 */
public class DropTierOneFieldsTransformer implements CsvTransformer {

    private final String idField;

    private final GlobalCodeBookFactory globalCodeBookFactory;

    public DropTierOneFieldsTransformer(String idField, GlobalCodeBookFactory globalCodeBookFactory) {
        this.idField = idField;
        this.globalCodeBookFactory = globalCodeBookFactory;
    }

    @Override
    public List<Csv> transformCsv(Csv csv) {
        var globalCodeBook = globalCodeBookFactory.getGlobalCodeBook();
        var idFieldIndex = csv.getIndex(idField);
        if(idFieldIndex == -1) {
            return List.of(csv);
        }
        var filtered = csv.content()
                .stream()
                .filter(row -> {
                    var id = row.get(idFieldIndex);
                    return !globalCodeBook.isVariable(id);
                })
                .toList();
        return List.of(new Csv(csv.coordinates(), csv.header(), filtered));
    }
}
