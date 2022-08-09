package edu.stanford.radx;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-01
 */
public class DataDictionaryDescriptiveStatsSummarizer implements DataDictionariesProcessor {

    private static final String DATA_DICTIONARY_STATS_CSV = "data-dictionary-stats.csv";

    private List<CsvFileContents> dataDictionaries = new ArrayList<>();

    public DataDictionaryDescriptiveStatsSummarizer() {
    }

    @Override
    public void beginProcessing() {
        dataDictionaries.clear();
    }

    @Override
    public void processDataDictionary(CsvFileContents dataDictionary, Path outputDirectory) {
        dataDictionaries.add(dataDictionary);

    }

    @Override
    public void endProcessing(Path outputPath) {
        try {
            var output = Files.newBufferedWriter(outputPath.resolve(DATA_DICTIONARY_STATS_CSV));
            var printer = new CSVPrinter(output, CSVFormat.DEFAULT);
            printer.printRecord("Data Dictionary", "Number of Columns", "Number of Rows");
            dataDictionaries.stream()
                            .sorted()
                            .forEach(dataDictionary -> {
                try {
                    var reader = new StringReader(dataDictionary.csvContent());
                    var parser = new CSVParser(reader, CSVFormat.DEFAULT);
                    var records = parser.getRecords();
                    printer.printRecord(List.of(dataDictionary.coordinates().getDescription(),
                                                records.get(0).size(),
                                                records.size() - 1));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            printer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
