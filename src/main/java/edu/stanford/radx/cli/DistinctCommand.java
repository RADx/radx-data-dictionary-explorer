package edu.stanford.radx.cli;

import edu.stanford.radx.DistinctRowsTransformer;
import org.springframework.stereotype.Component;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Mixin;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-22
 */
@Component
@Command(name = "distinct", description = "Outputs CSV with distinct rows")
public class DistinctCommand implements CliCommand {

    @Mixin
    IoMixin io;

    private final CliCsvTransformerProcessor processor;

    public DistinctCommand(CliCsvTransformerProcessor processor) {
        this.processor = processor;
    }

    @Override
    public Integer call() throws Exception {
        processor.transformCsvFiles(io.in, io.out, new DistinctRowsTransformer());
        return 0;
    }
}
