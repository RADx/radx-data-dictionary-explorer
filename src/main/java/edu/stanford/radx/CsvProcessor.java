package edu.stanford.radx;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-06-28
 */
public class CsvProcessor {

    public List<CsvFieldDescriptor> processCsvFile(CsvFileContents csvFileContents) {
        try {
            try {
                var pth = Path.of("/tmp/converted/" + csvFileContents.coordinates().getDescription() + ".csv");
                Files.createDirectories(pth.getParent());
                Files.writeString(pth, csvFileContents.csvContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
            var format = CSVFormat.DEFAULT;
            var parser = CSVParser.parse(csvFileContents.csvContent(), format);
            var records = parser.getRecords();
            return getCsvFieldDescriptors(csvFileContents.coordinates(), records);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private List<CsvFieldDescriptor> getCsvFieldDescriptors(CsvCoordinates coordinates, List<CSVRecord> records) {
        if (records.isEmpty()) {
            return Collections.emptyList();
        }
        var columnDescriptors = new ArrayList<CsvFieldDescriptor>();
        var headerRecord = records.get(0);
        var headerRecordSize = headerRecord.size();
        for (int i = 0; i < headerRecordSize; i++) {
            final int columnIndex = i;
            var value = headerRecord.get(columnIndex);
            if (!value.isBlank()) {
                var columnName = value.trim().toLowerCase();
                var sampleValues = records.stream()
                                          // Skip over header row
                                          .skip(1)
                                          .filter(record -> columnIndex < record.size())
                                          .map(record -> record.get(columnIndex))
                                          .filter(v -> !v.isBlank())
                                          .distinct()
                                          .sorted(String::compareToIgnoreCase)
                                          .collect(Collectors.toList());
                var columnDescriptor = new CsvFieldDescriptor(coordinates,
                                                              columnIndex,
                                                              columnName,
                                                              List.copyOf(sampleValues));
                columnDescriptors.add(columnDescriptor);
            }
        }
        return columnDescriptors;
    }

}
