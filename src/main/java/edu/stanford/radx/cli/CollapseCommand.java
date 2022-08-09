package edu.stanford.radx.cli;

import edu.stanford.radx.CollapseTransformer;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

import java.util.regex.Pattern;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Help;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-22
 */
@Component
@Command(name = "collapse", description = "Collapses sequences of rows in a CSV file into single rows.  Collapsing is performed based on a 'key field'.  Sequential rows that have the same key are collapsed into the same row.  All fields are preserved.  Multiple values that a collapsed into the same field value are separated by a specified delimeter.")
public class CollapseCommand implements CliCommand {

    @Mixin
    IoMixin io;

    @Option(names = "--key-field-name", required = true, description = "The name of the field that contains the values used to generate keys that are used to merge multiple rows into single rows.  Rows with the same keys will be collapsed into the same row.")
    public String keyFieldName;

    @Option(names = "--key-field-regex", required = true, description = "A regular expression that is used to match values in the key column as keys.  The regular expression matches are used to derive keys from the values in the key field.  For example, ^([^-]+\\-\\d+) matches a sequence of characters that end in a hyphen followed by an integer.  Apple-1, Apple-2, Apple-33 would be matched as keys.")
    public Pattern keyFieldRegex;

    @Option(names = "--key-field-regex-group", required = true, description = "The group of the regular expression specified by the --key-field-regex that is used to generate the key.  For example, if --key-field-regex was specified as ^([^-]+\\-\\d+) and the key field regex group is specified as 1 then the key Apple would be extracted for Apple-1, Apple-2, Apple-33")
    public int keyFieldPatternGroup = 0;

    @Option(names = "--delimeter", required = true, defaultValue = ";", showDefaultValue = Help.Visibility.ALWAYS, description = "Specifies the separator that should be used when multiple values are merged into a single CSV cell.  For example if ValA and ValB were merged then if the delimeter is a semi-colon the resulting value would be ValA;ValB")
    public String delimeter;

    private final CliCsvTransformerProcessor transformer;

    public CollapseCommand(CliCsvTransformerProcessor transformer) {
        this.transformer = transformer;
    }

    @Override
    public Integer call() throws Exception {
        var collapser = new CollapseTransformer(keyFieldName, keyFieldRegex, keyFieldPatternGroup,
                                                delimeter);
        transformer.transformCsvFiles(io.in, io.out, collapser);
        return 0;
    }
}
