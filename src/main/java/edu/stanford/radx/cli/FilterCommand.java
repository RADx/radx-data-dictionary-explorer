package edu.stanford.radx.cli;

import edu.stanford.radx.FilteringCsvTransformer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static picocli.CommandLine.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-22
 */
@Component
@Command(name = "filter", description = "Filters rows in the CSV by regular expression patterns.  Regular expression filters can be specified on a per field basis.  Only field values that match the specified patterns will be included in the output.")
public class FilterCommand implements CliCommand {

    @Mixin
    IoMixin io;

    @Option(names = "--field-value-filter", split = ",", description = "Filter the specified field with the specified regular expression. Syntax is fieldName=regex.  Multiple filteres should be separated by commas.")
    public List<String> fieldNameFilters = new ArrayList<>();

    private final CliCsvTransformerProcessor processor;

    public FilterCommand(CliCsvTransformerProcessor processor) {
        this.processor = processor;
    }

    @Override
    public Integer call() throws Exception {
        var patterns = getFieldPatternFilters();
        processor.transformCsvFiles(io.in, io.out, new FilteringCsvTransformer(patterns));
        return 0;
    }

    private Map<String, Pattern> getFieldPatternFilters() {
        var filters = new HashMap<String, Pattern>();
        for (String fieldNameFilter : fieldNameFilters) {
            var split = fieldNameFilter.split("=");
            if (split.length != 2) {
                System.err.println("Invalid field name pattern: " + fieldNameFilter);
            }
            else {
                var fieldName = split[0];
                var regex = split[1];
                try {
                    var pattern = Pattern.compile(regex);
                    filters.put(fieldName, pattern);
                } catch (PatternSyntaxException e) {
                    System.err.println("ERROR!  Invalid regular expression for field name pattern: " + fieldNameFilter);
                }
            }
        }
        return filters;
    }
}
