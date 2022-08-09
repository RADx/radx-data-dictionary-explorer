package edu.stanford.radx.cli;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

import static picocli.CommandLine.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-22
 */
@Component
@Command(name = "make-csv", description = "Converts Excel files to CSV files, parses all CSV files and then tidies them by padding short rows and removing empty columns.")
public class CsvCommand implements CliCommand {

    @Mixin
    IoMixin io;

    @Option(names = "--append-source", description = "Adds two columns to the CSV to indicate the source file name and the source directory.")
    public boolean addSourceFileColumn = false;

    @Option(names = "--verbose")
    public boolean verbose = false;

    private final CliInputFilesProcessor processor;

    public CsvCommand(CliInputFilesProcessor processor) {
        this.processor = processor;
    }

    @Override
    public Integer call() throws Exception {
        var counter = new AtomicInteger();
        processor.processInputFiles(io.in, csv -> {
            counter.incrementAndGet();
            if (verbose) {
                System.err.println("Processing " + csv.coordinates().getDescription());
            }
            var outCsv = csv.padShortRowsToHeader().withoutEmptyColumns();
            if(addSourceFileColumn) {
                outCsv = outCsv.withSource();

            }
            var writer = new CliCsvWriter();
            writer.writeCsv(io.out, outCsv);
        });
        if (verbose) {
            System.err.printf("Generated %d CSV files\n", counter.get());
        }
        return 0;
    }
}
