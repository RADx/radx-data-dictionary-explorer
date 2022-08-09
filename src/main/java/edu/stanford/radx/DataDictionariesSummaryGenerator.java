package edu.stanford.radx;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-06-28
 */
public class DataDictionariesSummaryGenerator {

    private final InputFilesProcessor inputFilesProcessor;

    private final List<DataDictionariesProcessor> dataDictionariesProcessors;

    public DataDictionariesSummaryGenerator(InputFilesProcessor inputFilesProcessor,
                                            List<DataDictionariesProcessor> dataDictionariesProcessors) {
        this.inputFilesProcessor = inputFilesProcessor;
        this.dataDictionariesProcessors = dataDictionariesProcessors;
    }

    /**
     * Process the files under the input directory, scanning for and processing data dictionaries.
     */
    public void processInputFiles(List<Path> input, Path output) throws IOException {
        dataDictionariesProcessors.forEach(DataDictionariesProcessor::beginProcessing);
        inputFilesProcessor.processInputFiles(input, csvFileContents -> {
            dataDictionariesProcessors.forEach(processor -> processor.processDataDictionary(csvFileContents, output));
        });
        dataDictionariesProcessors.forEach(dataDictionariesProcessor -> dataDictionariesProcessor.endProcessing(output));
    }
}
