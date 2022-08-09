package edu.stanford.radx.cli;

import com.google.common.collect.LinkedHashMultiset;
import edu.stanford.radx.CsvStreamTransformer;
import edu.stanford.radx.FieldNameSynonyms;
import edu.stanford.radx.FieldNameSynonymsLoader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.function.Predicate.not;
import static picocli.CommandLine.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-20
 */
@Component
@Command(name = "field-names", description = "Lists the field names used in data dictionaries along with their normalized names")
public class FieldNamesCommand implements CliCommand {


    private static final String STANDARD_FIELD_NAME = "Standard field name";

    private static final String FIELD_NAME = "Field name";

    private static final String OCCURRENCES = "Occurrences";

    @Mixin
    IoMixin io;

    @Option(names = "--field-names-map", description = "Path to a comma separated values (CSV) file that contains mappings of field name synonyms to field names.")
    public String fieldNamesMap;

    @Option(names = "--distinct", description = "Eliminated duplicate rows")
    boolean distinct = false;

    @Option(names = "--sorted", description = "Sort output so that rows in a CSV file are lexicographically sorted.  Note that case is ignored when sorting is performed.")
    boolean sorted = false;

    @Option(names = "--lowercase", description = "Transform all CSV data to lowercase.  This operation is performed before the distinct operation.")
    boolean lowercase = false;

    @Option(names = "--collapse-white-space", description = "Transform all CSV cell data to collapse all runs of white space (including new lines) into single spaces.")
    boolean collapseWhiteSpace = false;

    private final CliInputFilesProcessor inputFilesProcessor;

    private final FieldNameSynonymsLoader fieldNameSynonymsLoader;

    private final FieldNameSynonyms syns;

    public FieldNamesCommand(CliInputFilesProcessor inputFilesProcessor,
                             FieldNameSynonymsLoader fieldNameSynonymsLoader,
                             FieldNameSynonyms syns) {
        this.inputFilesProcessor = inputFilesProcessor;
        this.fieldNameSynonymsLoader = fieldNameSynonymsLoader;
        this.syns = syns;
    }

    @Override
    public Integer call() throws Exception {
            var collected = LinkedHashMultiset.<String>create();
            var csvTransformer = new CsvStreamTransformer(Collections.emptyList(), distinct, collapseWhiteSpace, lowercase, sorted,
                                                          false);
            inputFilesProcessor.processInputFiles(io.in, csv -> collected.addAll(csv.header()));
            fieldNameSynonymsLoader.load(Optional.ofNullable(fieldNamesMap).map(Path::of).orElse(null));
            var mapped = collected.stream()
                                  .filter(not(String::isBlank))
                                  .map(fieldName -> List.of(fieldName, Integer.toString(collected.count(fieldName)), syns.getCanonicalFieldName(fieldName)));

            var transformed = csvTransformer.transform(mapped).toList();

            var printer = new CsvConsolePrinter(System.out);
            printer.print(List.of(FIELD_NAME, OCCURRENCES, STANDARD_FIELD_NAME), transformed);

            var csvBuffer = new StringBuilder();
            var csvPrinter = new CSVPrinter(csvBuffer, CSVFormat.DEFAULT);
            csvPrinter.printRecord(List.of(FIELD_NAME, OCCURRENCES, STANDARD_FIELD_NAME));
            transformed.forEach(row -> {
                try {
                    csvPrinter.printRecord(row);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            var parentPath = io.out.getParent();
            if(!Files.exists(parentPath)) {
                Files.createDirectories(parentPath);
            }
            Files.writeString(io.out, csvBuffer);
        return 0;
    }
}
