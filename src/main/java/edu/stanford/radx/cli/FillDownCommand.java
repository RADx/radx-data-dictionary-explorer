package edu.stanford.radx.cli;

import edu.stanford.radx.FillDownCsvTransformer;
import org.springframework.stereotype.Component;

import java.util.List;

import static picocli.CommandLine.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-22
 */
@Component
@Command(name = "fill-down", description = "Fills down empty cells in a CSV.  Cells are filled with the first non-blank preceding cell value in the same column.")
public class FillDownCommand implements CliCommand {


    @Mixin
    IoMixin io;

    @Option(names = "--field-names", split = ",", required = true, description = "The name of the column to fill down.  The specified name must exactly match the header name for the target column.")
    List<String> fieldNames;

    private final CliCsvTransformerProcessor processor;

    public FillDownCommand(CliCsvTransformerProcessor processor) {
        this.processor = processor;
    }

    @Override
    public Integer call() throws Exception {
        processor.transformCsvFiles(io.in, io.out, new FillDownCsvTransformer(fieldNames));
        return 0;
    }
}
