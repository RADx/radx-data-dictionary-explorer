package edu.stanford.radx.cli;

import edu.stanford.radx.ReplaceTransformer;
import edu.stanford.radx.RowFilter;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-11-28
 */
@Component
@Command(name = "replace", description = "Replaces text in cells")
public class ReplaceCommand implements CliCommand {

    private final CliCsvTransformerProcessor processor;

    @Mixin
    IoMixin io;

    @Mixin
    FilterMixin filter;

    @Option(names = "--find", required = true, description = "A regular expression to find in cells")
    String find;

    @Option(names = "--replace", required = true, description = "A string to replace the found strings.  Regular expression capturing groups can be used.")
    String replace;

    public ReplaceCommand(CliCsvTransformerProcessor processor) {
        this.processor = processor;
    }

    @Override
    public Integer call() throws Exception {
        try {
            var pattern = Pattern.compile(find);
            var transformer = new ReplaceTransformer(new RowFilter(filter.getFilters()), pattern, replace);
            processor.transformCsvFiles(io.in, io.out, transformer);
            return 0;
        } catch (PatternSyntaxException e) {
            System.err.println("Malformed search string: " + e.getMessage());
            return 1;
        }

    }
}
