package edu.stanford.radx.cli;

import edu.stanford.radx.Csv;
import edu.stanford.radx.CsvTransformer;
import edu.stanford.radx.GlobalCodeBookFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-07-11
 */
@Component
public class AppendGlobalCodeBookTransformer implements CsvTransformer {

    private final GlobalCodeBookFactory globalCodeBookFactory;

    public AppendGlobalCodeBookTransformer(GlobalCodeBookFactory globalCodeBookFactory) {
        this.globalCodeBookFactory = globalCodeBookFactory;
    }

    @Override
    public List<Csv> transformCsv(Csv csv) {
        var gcb = globalCodeBookFactory.getGlobalCodeBook();
        var idIndex = csv.getIndex("Id");
        var labelIndex = csv.getIndex("Label");
        var enumerationIndex = csv.getIndex("Enumeration");
        var sectionIndex = csv.getIndex("Section");
        var sourceFileIndex = csv.getIndex("source_file");
        var sourceDirectoryIndex = csv.getIndex("source_directory");
        var header = csv.header();
        var content = new ArrayList<>(csv.content());
        gcb.getRecords()
                .forEach(rec -> {
                    var blankRow = new ArrayList<String>();
                    for(int i = 0; i < header.size(); i++) {
                        blankRow.add("");
                    }
                    if (idIndex != -1) {
                        blankRow.set(idIndex, rec.variable());
                    }
                    if (sectionIndex != -1) {
                        blankRow.set(sectionIndex, rec.concept());
                    }
                    if (labelIndex != -1) {
                        blankRow.set(labelIndex, rec.prompt());
                    }
                    if (enumerationIndex != -1) {
                        blankRow.set(enumerationIndex, rec.responses());
                    }
                    if(sourceFileIndex != -1) {
                        blankRow.set(sourceFileIndex, "Global Code Book");
                    }
                    if(sourceDirectoryIndex != -1) {
                        blankRow.set(sourceDirectoryIndex, "Global Code Book");
                    }
                    content.add(blankRow);
                });
        return List.of(new Csv(csv.coordinates(),
                               header,
                               content));
    }
}
