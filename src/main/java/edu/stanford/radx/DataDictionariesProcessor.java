package edu.stanford.radx;

import java.nio.file.Path;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-01
 */
public interface DataDictionariesProcessor {

    void beginProcessing();

    void processDataDictionary(CsvFileContents dataDictionary, Path outputDirectory);

    void endProcessing(Path outputPath);
}
