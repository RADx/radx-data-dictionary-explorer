package edu.stanford.radx.cli;

import edu.stanford.radx.RenameFieldTransformer;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-12-07
 */
@Component
@Command(name = "rename-field", description = "Renames a field (a column header)")
public class RenameFieldCommand implements CliCommand {

    @Mixin
    IoMixin io;

    @Option(names = "--field-name")
    private String fieldName;

    @Option(names = "--new-name")
    private String newName;

    private final CliCsvTransformerProcessor processor;

    public RenameFieldCommand(CliCsvTransformerProcessor processor) {
        this.processor = processor;
    }

    @Override
    public Integer call() throws Exception {
        processor.transformCsvFiles(io.in,
                                    io.out,
                                    new RenameFieldTransformer(fieldName, newName));
        return 0;
    }
}
