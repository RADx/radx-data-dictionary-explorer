package edu.stanford.radx.cli;

import edu.stanford.radx.RetainFieldsTransformer;
import org.springframework.stereotype.Component;

import java.util.List;

import static picocli.CommandLine.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-25
 */
@Component
@Command(name = "retain-fields", description = "Retains the specified fields in the CSV file.  All other fields will be dropped.")
public class RetainFieldsCommand implements CliCommand {

    @Mixin
    IoMixin io;

    @Option(names = "--field-names", split = ",", description = "The names of fields to retain.  The specified names must exactly match the field names in the CSV header.")
    public List<String> fieldNames;

    private final CliCsvTransformerProcessor processor;

    public RetainFieldsCommand(CliCsvTransformerProcessor processor) {
        this.processor = processor;
    }

    @Override
    public Integer call() throws Exception {
        processor.transformCsvFiles(io.in, io.out, new RetainFieldsTransformer(fieldNames));
        return 0;
    }
}
