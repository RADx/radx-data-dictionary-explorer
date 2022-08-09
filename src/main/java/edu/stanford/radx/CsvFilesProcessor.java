package edu.stanford.radx;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-21
 */
public class CsvFilesProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CsvFilesProcessor.class);

    private final InputFilesProcessor inputFilesProcessor;

    public CsvFilesProcessor(InputFilesProcessor inputFilesProcessor) {
        this.inputFilesProcessor = inputFilesProcessor;
    }

    public void processCsvFiles(List<Path> inputPaths, Consumer<Csv> csvHandler) throws IOException {
        inputFilesProcessor.processInputFiles(inputPaths, contents -> {
            try {
                var parser = new CSVParser(new StringReader(contents.csvContent()), CSVFormat.DEFAULT);
                var records = parser.getRecords();
                var header = records.stream()
                        .map(CSVRecord::toList)
                        .findFirst()
                        .orElse(Collections.emptyList());
                var data = records.stream()
                                  .skip(1)
                        .map(CSVRecord::toList)
                        .toList();
                try {
                    csvHandler.accept(new Csv(contents.coordinates(),
                                              header,
                                              data));
                } catch (Exception e) {
                    logger.error("Exception thrown by CSV handler", e);
                }
            } catch (Exception e) {
                logger.error("Error parsing CSV file", e);
            }
        });
    }
}
