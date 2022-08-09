package edu.stanford.radx.cli;

import edu.stanford.radx.Csv;
import edu.stanford.radx.CsvFilesProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-20
 */
public class CliInputFilesProcessor {

    private static final Logger logger = LoggerFactory.getLogger(CliInputFilesProcessor.class);

    private final CsvFilesProcessor processor;

    public CliInputFilesProcessor(CsvFilesProcessor processor) {
        this.processor = processor;
    }

    public void processInputFiles(List<Path> inputFiles, Consumer<Csv> consumer) throws IOException {
        var paths = inputFiles.stream()
                .peek(filePath -> {
                    if(!Files.exists(filePath)) {
                        logger.info("Input file does not exist: {}", filePath);
                    }
                })
                          .filter(Files::exists)
                          .toList();
        processor.processCsvFiles(paths, consumer);
    }
}
