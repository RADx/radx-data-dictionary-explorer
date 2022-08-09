package edu.stanford.radx.cli;

import edu.stanford.radx.CsvTransformer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-25
 */
public class CliCsvTransformerProcessor {

    private final CliInputFilesProcessor inputFilesProcessor;

    private final CliCsvWriter writer;

    public CliCsvTransformerProcessor(CliInputFilesProcessor inputFilesProcessor, CliCsvWriter writer) {
        this.inputFilesProcessor = inputFilesProcessor;
        this.writer = writer;
    }

    public void transformCsvFiles(List<Path> in,
                                  Path out,
                                  CsvTransformer transformer) throws IOException {
        inputFilesProcessor.processInputFiles(in, csv -> {
            try {
                var transformedCsvs = transformer.transformCsv(csv);
                for(var transformedCsv : transformedCsvs) {
                    writer.writeCsv(out, transformedCsv);
                }
            } catch (Exception e) {
                System.err.printf("Error when processing %s: %s\n", csv.coordinates().getDescription(), e.getMessage());
            }
        });
    }
}
