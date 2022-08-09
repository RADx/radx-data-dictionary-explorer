package edu.stanford.radx;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-01
 */
public class DataDictionariesMergingProcessor implements DataDictionariesProcessor {

    private static Logger logger = LoggerFactory.getLogger(DataDictionariesMergingProcessor.class);

    private static final String MERGED_CSV = "merged.csv";

    private Map<String, Integer> fieldNames = new LinkedHashMap<>();

    private List<CsvFileContents> dataDictionaries = new ArrayList<>();

    private List<List<Integer>> indexMappings = new ArrayList<>();

//    private Map<String, String> canonicalFieldNames = new HashMap<>();

    private FieldNameSynonyms fieldNameSynonyms;

    public DataDictionariesMergingProcessor(FieldNameSynonyms fieldNameSynonyms) {
        this.fieldNameSynonyms = fieldNameSynonyms;
    }

    @Override
    public void beginProcessing() {
        fieldNames.clear();
        dataDictionaries.clear();
    }

    @Override
    public void processDataDictionary(CsvFileContents dataDictionary, Path outputDirectory) {
        try {
            CSVParser parser = new CSVParser(new StringReader(dataDictionary.csvContent()), CSVFormat.DEFAULT);
            var records = parser.getRecords();
            if (records.isEmpty()) {
                return;
            }
            var headerRecord = records.get(0);
            var indexMapping = new ArrayList<Integer>(headerRecord.size());
            for (int i = 0; i < headerRecord.size(); i++) {
                var fieldName = headerRecord.get(i);
                var canonicalFieldName = fieldNameSynonyms.getCanonicalFieldName(fieldName);
                if(!canonicalFieldName.isBlank()) {
                    var existingColumnIndex = fieldNames.get(canonicalFieldName);
                    if (existingColumnIndex == null) {
                        int mergedColumnIndex = fieldNames.size();
                        fieldNames.put(canonicalFieldName, mergedColumnIndex);
                        indexMapping.add(mergedColumnIndex);
                    }
                    else {
                        indexMapping.add(existingColumnIndex);
                    }
                }

            }
            dataDictionaries.add(dataDictionary);
            indexMappings.add(indexMapping);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void endProcessing(Path outputDirectory) {
        try {
            System.out.printf("Processed %d Data Dictionaries with %d unique fields\n",
                              dataDictionaries.size(),
                              fieldNames.size());
            fieldNames.keySet().forEach(fn -> System.out.printf("    \"%s\"\n", fn.replace("\n", "\\n")));

            if (!Files.exists(outputDirectory)) {
                Files.createDirectories(outputDirectory);
            }
            var bufferedWriter = Files.newBufferedWriter(outputDirectory.resolve(MERGED_CSV));
            var printer = new CSVPrinter(bufferedWriter, CSVFormat.DEFAULT);
            var headers = new ArrayList<>();
            headers.add("source");
            headers.addAll(fieldNames.keySet());
            printer.printRecord(headers);
            for (int dataDictionaryIndex = 0; dataDictionaryIndex < dataDictionaries.size(); dataDictionaryIndex++) {
                var dataDictionary = dataDictionaries.get(dataDictionaryIndex);
                var mappings = indexMappings.get(dataDictionaryIndex);
                CSVParser parser = new CSVParser(new StringReader(dataDictionary.csvContent()), CSVFormat.DEFAULT);
                parser.stream()
                      // Skip header row
                      .skip(1).map(record -> getMergedCsvRow(dataDictionary, mappings, record)).forEach(row -> printRow(printer, row));
                parser.close();
            }
            bufferedWriter.close();
            printer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Object[] getMergedCsvRow(CsvFileContents dataDictionary, List<Integer> mappings, CSVRecord record) {
        var mergedRow = new String[fieldNames.size() + 1];
        Arrays.fill(mergedRow, "");
        mergedRow[0] = dataDictionary.coordinates().getDescription();
        for (int columnIndex = 0; columnIndex < record.size(); columnIndex++) {
            if (columnIndex < mappings.size()) {
                var mappedIndex = mappings.get(columnIndex) + 1;
                mergedRow[mappedIndex] = record.get(columnIndex);
            }
        }
        return mergedRow;
    }

    private void printRow(CSVPrinter printer, Object[] row) {
        try {
            printer.printRecord(row);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
