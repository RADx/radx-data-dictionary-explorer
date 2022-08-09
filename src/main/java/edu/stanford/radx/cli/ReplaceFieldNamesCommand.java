package edu.stanford.radx.cli;

import edu.stanford.radx.Csv;
import edu.stanford.radx.FieldNameSynonyms;
import edu.stanford.radx.FieldNameSynonymsLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static picocli.CommandLine.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-22
 */
@Component
@Command(name = "replace-field-names", description = "Replaces field names so that source field names (local field names) are replaced with preferred/canonical field names as defined in the field names input map.  If no field-names-map is specified then the built in map is used.")
public class ReplaceFieldNamesCommand implements CliCommand {

    @Mixin
    IoMixin io;

    @Option(names = "--mapping-file", description = "Path to a comma separated values (CSV) file that has two columns and contains mappings of names to names.  Names in the first column will be replaced by names in the second column.")
    public String mappingFile;

    private final CliInputFilesProcessor csvFilesProcessor;

    private final FieldNameSynonymsLoader fieldNameSynonymsLoader;

    private final FieldNameSynonyms fieldNameSynonyms;

    private final CliCsvWriter csvWriter;

    public ReplaceFieldNamesCommand(CliInputFilesProcessor csvFilesProcessor,
                                    FieldNameSynonymsLoader fieldNameSynonymsLoader,
                                    FieldNameSynonyms fieldNameSynonyms, CliCsvWriter csvWriter) {
        this.csvFilesProcessor = csvFilesProcessor;
        this.fieldNameSynonymsLoader = fieldNameSynonymsLoader;
        this.fieldNameSynonyms = fieldNameSynonyms;
        this.csvWriter = csvWriter;
    }

    @Override
    public Integer call() throws Exception {

        csvFilesProcessor.processInputFiles(io.in, csv -> {
            var mappedHeader = getOutputFieldNames(csv.header());
            var mappedCsv = new Csv(csv.coordinates(), mappedHeader, csv.content());
            csvWriter.writeCsv(io.out, mappedCsv);
        });
        return 0;
    }

    private Optional<Path> getFieldNamesMapPath() {
        if (mappingFile == null) {
            return Optional.empty();
        }
        var path = Path.of(mappingFile);
        if(Files.exists(path)) {
            return Optional.of(path);
        }
        else {
            System.err.println("Specified mapping file does not exist.  Using default mappings.");
            return Optional.empty();
        }
    }

    private List<String> getOutputFieldNames(List<String> rawFieldNames) {
        try {
            fieldNameSynonymsLoader.load(getFieldNamesMapPath().orElse(null));
            return rawFieldNames.stream()
                                .map(fieldNameSynonyms::getCanonicalFieldName)
                                .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
