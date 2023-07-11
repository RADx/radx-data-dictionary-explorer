package edu.stanford.radx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-06-30
 */
public class InputFilesProcessor {

    private static final Logger logger = LoggerFactory.getLogger(InputFilesProcessor.class);

    private static final int MAX_DEPTH = 10;

    private final Excel2Csv excel2Csv;

    public InputFilesProcessor(Excel2Csv excel2Csv) {
        this.excel2Csv = excel2Csv;
    }

    public void processInputFiles(List<Path> inputPaths, Consumer<CsvFileContents> csvHandler) throws IOException {
        logger.debug("Processing files");
        for (var inputPath : inputPaths) {
            try {
                if(!Files.exists(inputPath)) {
                    logger.debug("Input path does not exist ({})", inputPath);
                    return;
                }
                if(Files.isDirectory(inputPath)) {
                    var walker = Files.walk(inputPath, MAX_DEPTH, FileVisitOption.FOLLOW_LINKS);
                    walker.sorted().forEach(p -> {
                        processPotentialDataDictionaryFile(p, csvHandler);
                    });
                }
                else {
                    processPotentialDataDictionaryFile(inputPath, csvHandler);
                }
            } catch (IOException e) {
                logger.debug("Could not process path: {}", inputPath, e);
            }

        }
    }

    private void processPotentialDataDictionaryFile(Path filePath, Consumer<CsvFileContents> csvHandler) {
        if(isExcelWorkbook(filePath)) {
            logger.debug("Processing Excel workbook {}", filePath);
            var csvSheets = excel2Csv.extractCsvFiles(filePath);
            csvSheets.forEach(csvSheet -> csvHandler.accept(csvSheet.getCsvFileContents()));
        }
        else if(isCsvFile(filePath)) {
            try {
                logger.debug("Processing CSV file {}", filePath);
                var csvContents = Files.readString(filePath);
                csvHandler.accept(new CsvFileContents(new PlainCsvFileCoordinates(filePath), csvContents));
            } catch (Exception e) {
                logger.debug("Could not read file: {}.  {}", filePath, e.getMessage());
            }
        }
    }

    private static boolean isCsvFile(Path filePath) {
        return filePath.getFileName().toString().endsWith(".csv");
    }

    private static boolean isExcelWorkbook(Path filePath) {
        var fileName = filePath.getFileName().toString();
        return !fileName.startsWith("~$") && fileName.endsWith(".xlsx");
    }
}
