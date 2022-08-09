package edu.stanford.radx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-05
 */
public class DataDictionariesCsvOutProcessor implements DataDictionariesProcessor {

    private Logger logger = LoggerFactory.getLogger(DataDictionariesCsvOutProcessor.class);

    public DataDictionariesCsvOutProcessor() {
    }

    @Override
    public void beginProcessing() {

    }

    @Override
    public void processDataDictionary(CsvFileContents dataDictionary, Path outputDirectory) {
        try {
            var outputFileName = getOutputFileName(dataDictionary);
            var outputPath = outputDirectory.resolve("processed-data-dictionaries")
                                            .resolve(dataDictionary.coordinates().getParentDirectory().getFileName())
                    .resolve(outputFileName);
            Files.createDirectories(outputPath.getParent());
            Files.writeString(outputPath, dataDictionary.csvContent());
        } catch (IOException e) {
            logger.error("Error while outputing CSV files");
        }
    }

    private String getOutputFileName(CsvFileContents dataDictionary) {
        var desc = dataDictionary.coordinates().getDescription();
        if(desc.endsWith(".csv")) {
            return desc;
        }
        else {
            return desc + ".csv";
        }
    }

    @Override
    public void endProcessing(Path outputPath) {

    }
}
