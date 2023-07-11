package edu.stanford.radx.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.stanford.radx.Csv2JsonColumns;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-11-30
 */
@Component
@Command(name = "csv2json", description = "Convert a CSV file to JSON")
public class Csv2JsonCommand implements CliCommand {

    @Mixin
    IoMixin io;

    @Option(names = "--key", required = true)
    String key;

    @Option(names = "--multi-valued-keys", split = ",")
    Set<String> multiValuedKeys = new HashSet<>();

    @Option(names = "--split-value-keys", split = ",")
    Set<String> splitValueKeys = new HashSet<>();

    @Option(names = "--excluded-keys", split = ",")
    Set<String> excludedKeys = new HashSet<>();

    private final CliInputFilesProcessor processor;

    private final Csv2JsonColumns csv2Json;

    private final ObjectMapper mapper;

    public Csv2JsonCommand(CliInputFilesProcessor processor, Csv2JsonColumns csv2Json, ObjectMapper mapper) {
        this.processor = processor;
        this.csv2Json = csv2Json;
        this.mapper = mapper;
    }

    @Override
    public Integer call() throws Exception {
        processor.processInputFiles(io.in, csv -> {
            try {
                var json = csv2Json.convertToJson(csv, key, multiValuedKeys, splitValueKeys, excludedKeys);
                var outPath = io.out;
                var outputDirectory = outPath.resolve(csv.coordinates().getParentDirectory().getFileName());
                Files.createDirectories(outputDirectory);
                var outFile = outputDirectory
                                     .resolve(csv.coordinates().getDescription() + ".json").toFile();
                mapper.writerWithDefaultPrettyPrinter().writeValue(outFile, json);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });
        return 0;
    }
}

