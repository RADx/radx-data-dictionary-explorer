package edu.stanford.radx.cli;

import edu.stanford.radx.StringTransform;
import edu.stanford.radx.StringTransformer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static picocli.CommandLine.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-22
 */
@Component
@Command(name = "transform", description = "Transforms data in CSV files by performing user specified string operations")
public class TransformDataCommand implements CliCommand {

    @Mixin
    IoMixin io;

    @Option(names = "--field-names", split = ",", description = "The name of the field names to process.")
    public List<String> fieldNames = Collections.emptyList();

    @Option(names = "--lowercase", description = "Convert data values to lower case strings.")
    boolean lowercase = false;

    @Option(names = "--uppercase", description = "Convert data values to upper case strings.")
    boolean uppercase = false;

    @Option(names = "--collapse-white-space", description = "Replace new line characters with spaces and then collapse repeated occurrences of white space, including new line characters.")
    boolean collapseWhiteSpace = false;

    private final CliCsvTransformerProcessor processor;

    public TransformDataCommand(CliCsvTransformerProcessor processor) {
        this.processor = processor;
    }

    @Override
    public Integer call() throws Exception {
        var transforms = new ArrayList<StringTransform>();
        if(lowercase) {
            transforms.add(StringTransform.TO_LOWER_CASE);
        }
        if(uppercase) {
            transforms.add(StringTransform.TO_UPPER_CASE);
        }
        if(collapseWhiteSpace) {
            transforms.add(StringTransform.COLLAPSE_WHITE_SPACE);
        }
        processor.transformCsvFiles(io.in, io.out, new StringTransformer(fieldNames, transforms));
        return 0;
    }
}
