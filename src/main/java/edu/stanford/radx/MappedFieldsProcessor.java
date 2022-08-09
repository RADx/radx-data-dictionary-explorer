package edu.stanford.radx;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-06
 */
public class MappedFieldsProcessor implements DataDictionariesProcessor {

    private final FieldNameSynonyms fieldNameSynonyms;

    private static final String outputFileName = "mapped-fields-summary.csv";

    private Logger logger = LoggerFactory.getLogger(MappedFieldsProcessor.class);

    public MappedFieldsProcessor(FieldNameSynonyms fieldNameSynonyms) {
        this.fieldNameSynonyms = fieldNameSynonyms;
    }

    @Override
    public void beginProcessing() {

    }

    @Override
    public void processDataDictionary(CsvFileContents dataDictionary, Path outputDirectory) {

    }

    @Override
    public void endProcessing(Path outputDirectory) {
        try {
            if(!Files.exists(outputDirectory)) {
                Files.createDirectories(outputDirectory);
            }
            var outputPath = outputDirectory.resolve(outputFileName);
            var printer = new CSVPrinter(Files.newBufferedWriter(outputPath), CSVFormat.DEFAULT);
            fieldNameSynonyms.getCanonicalFieldNamesMap()
                             .forEach((from, to) -> {
                                   try {
                                       printer.printRecord(from, to);
                                   } catch (IOException e) {
                                       logger.error("Error while printing CSV record", e);
                                   }
                               });
            printer.close();
        } catch (IOException e) {
            logger.error("Error while creating output", e);
        }
    }
}
