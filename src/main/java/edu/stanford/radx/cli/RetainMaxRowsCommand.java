package edu.stanford.radx.cli;

import com.google.common.collect.Ordering;
import edu.stanford.radx.Csv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static picocli.CommandLine.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-25
 */
@Component
@Command(name = "retain-max-rows", description = "Retains rows that have the maximum value in a specified field")
public class RetainMaxRowsCommand implements CliCommand {

    private static final Logger logger = LoggerFactory.getLogger(RetainMaxRowsCommand.class);

    @Mixin
    IoMixin io;

    @Option(names = "--field-names", required = true, description = "The name of the columns to extract a tuple from which will be used to group rows and obtain the maximum value")
    public String fieldNames;

    private final CliInputFilesProcessor processor;

    private final CliCsvWriter csvWriter;

    public RetainMaxRowsCommand(CliInputFilesProcessor processor, CliCsvWriter csvWriter) {
        this.processor = processor;
        this.csvWriter = csvWriter;
    }

    @Override
    public Integer call() throws Exception {
        processor.processInputFiles(io.in, csv -> {
            var fieldNamesToCompare = Arrays.asList(this.fieldNames.split(","));
            var fieldNameIndexes = fieldNamesToCompare.stream().map(csv::getIndex).toList();
            if (fieldNameIndexes.contains(-1)) {
                csvWriter.writeCsv(io.out, csv);
                return;
            }
            var keyedRows = csv.content().stream().map(row -> {
                var key = new ArrayList<Double>();
                for (var i : fieldNameIndexes) {
                    if (i < row.size()) {
                        var rowValue = row.get(i);
                        try {
                            var number = Double.parseDouble(rowValue);
                            key.add(number);
                        } catch (NumberFormatException e) {
                            logger.debug("Could not parse cell value into number", e);
                        }
                    }
                }
                return new KeyedRow(key, row);
            }).toList();

            var maxKey = keyedRows.stream().map(KeyedRow::key).max(Ordering.natural().lexicographical());

            if (maxKey.isEmpty()) {
                csvWriter.writeCsv(io.out, csv);
                return;
            }
            var mappedRows = keyedRows.stream()
                                      .collect(Collectors.groupingBy(kr -> kr.key, Collectors.toList()));
            var maxRows = mappedRows.get(maxKey.get()).stream().map(KeyedRow::row).toList();
            var maxCsv = new Csv(csv.coordinates(), csv.header(), maxRows);
            csvWriter.writeCsv(io.out, maxCsv);


        });
        return 0;
    }

    private record KeyedRow(List<Double> key,
                            List<String> row) {

    }
}
