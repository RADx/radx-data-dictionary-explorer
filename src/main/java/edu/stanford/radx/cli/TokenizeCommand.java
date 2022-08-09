package edu.stanford.radx.cli;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-08-09
 */
@Component
@CommandLine.Command(name = "append-tokens", description = "Appends the tokens from a set of fields that has been tokenized.")
public class TokenizeCommand implements CliCommand {

    @CommandLine.Mixin
    IoMixin io;

    @Option(names = "--field-names", split = ",", required = true, description = "The name of the column to fill down.  The specified name must exactly match the header name for the target column.")
    List<String> fieldNames;

    @Option(names = "--stop-words-file", description = "A path to a file containing a list of stop words.  The file should contain one stop word per line.")
    Path stopWordsFilePath;

    @Option(names = "--extra-stop-words", split = ",", description = "A comma separated list of extra stop-words to use during tokenization.")
    List<String> extraStopWords = new ArrayList<>();

    @Option(names = "--use-default-stop-words", description = "Use the default list of English stop words.", defaultValue = "false", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    boolean useDefaultStopWords = false;

    private final CliCsvTransformerProcessor processor;

    public TokenizeCommand(CliCsvTransformerProcessor processor) {
        this.processor = processor;
    }

    @Override
    public Integer call() throws Exception {
        var allStopWords = new ArrayList<String>(extraStopWords);
        if(stopWordsFilePath != null) {
            Files.readAllLines(stopWordsFilePath, StandardCharsets.UTF_8)
                                     .stream()
                                     .map(String::trim)
                                     .forEach(allStopWords::add);
        }
        if(useDefaultStopWords) {
            var stopWordsInputStream = this.getClass().getResourceAsStream("/stop-words.txt");
            if (stopWordsInputStream != null) {
                var reader = new InputStreamReader(stopWordsInputStream, Charsets.UTF_8);
                var bufferedReader = new BufferedReader(reader);
                bufferedReader.lines()
                        .map(String::trim)
                        .forEach(allStopWords::add);
            }
        }
        System.out.println(allStopWords);
        processor.transformCsvFiles(io.in, io.out, new AppendTokensProcessor(fieldNames, allStopWords));
        return 0;
    }

}
