package edu.stanford.radx.cli;

import edu.stanford.radx.CsvStreamTransformer;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.util.*;

import static picocli.CommandLine.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-22
 */
@Component
@Command(name = "merge", description = "Merges several input CSV files into a single output CSV file.  The field names in the output file are the union of the field names in the input files.  Field names are ordered as they are encountered in the input file with the exception of source_file and source_directory, which are appended to the end if present.")
public class MergeCommand implements CliCommand {

    @Mixin
    IoMixin io;

    @Option(names = "--distinct", description = "Output distinct rows by eliminating duplicate rows.")
    public boolean distinct = false;

    @Option(names = "--sorted", description = "Sort the output rows in lexicographic order comparing the string values of the cells.")
    public boolean sorted = false;

    @Option(names = "--verbose", description = "Output processing information")
    public boolean verbose = false;

    private final CliInputFilesProcessor inputFilesProcessor;

    public MergeCommand(CliInputFilesProcessor inputFilesProcessor) {
        this.inputFilesProcessor = inputFilesProcessor;
    }

    @Override
    public Integer call() throws Exception {
        var combinedFieldNames = new LinkedHashSet<String>();
        inputFilesProcessor.processInputFiles(io.in, csv -> {
            csv.header().stream().filter(s -> !s.isBlank()).forEach(combinedFieldNames::add);
        });
        if(combinedFieldNames.remove("source_file")) {
            combinedFieldNames.add("source_file");
        }
        if(combinedFieldNames.remove("source_directory")) {
            combinedFieldNames.add("source_directory");
        }
        if (verbose) {
            System.err.printf("Combining %d fields\n", combinedFieldNames.size());
        }

        var outIndexes = new LinkedHashMap<String, Integer>();
        combinedFieldNames.forEach(fieldName -> {
            outIndexes.put(fieldName, outIndexes.size());
            if (verbose) {
                System.err.printf("\t%s\n", fieldName);
            }
        });

        var outHeader = combinedFieldNames.stream().toList();
        var outRows = new ArrayList<List<String>>();
        inputFilesProcessor.processInputFiles(io.in, csv -> {
            var in2OutIndexesList = csv.header().stream()
                                       .filter(fieldName -> !fieldName.isBlank())
                                       .map(fieldName -> {
                                           var outIndex = outIndexes.get(fieldName);
                                           if(outIndex == null) {
                                               throw new RuntimeException("Missing index for '" + fieldName);
                                           }
                                           return outIndex;
                                       }).toList();



            csv.content()
               .stream()
               .map(inRow -> {
                   var outRow = new String[outHeader.size()];
                   Arrays.fill(outRow, "");

                   for(int i = 0; i < inRow.size(); i++) {
                       if (i < in2OutIndexesList.size()) {
                           int outIndex = in2OutIndexesList.get(i);
                           outRow[outIndex] = inRow.get(i);
                       }

                   }
                   return Arrays.stream(outRow).toList();
               }).forEach(outRows::add);

        });
        var transformedRows = new CsvStreamTransformer(Collections.emptyList(),
                                                       distinct,
                                                       false,
                                                       false,
                                                       sorted,
                                                       false).transform(outRows.stream()).toList();


        var buffer = new StringBuilder();
        var csvPrinter = new CSVPrinter(buffer, CSVFormat.DEFAULT);
        csvPrinter.printRecord(outHeader);
        csvPrinter.printRecords(transformedRows);
        csvPrinter.flush();

        Files.writeString(io.out, buffer);
        return 0;
    }
}
