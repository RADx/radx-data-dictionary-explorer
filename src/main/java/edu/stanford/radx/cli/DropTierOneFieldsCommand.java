package edu.stanford.radx.cli;

import edu.stanford.radx.GlobalCodeBookFactory;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-07-11
 */
@Component
@Command(name = "drop-tier-1-fields", description = "Drop fields that are RADx Tier 1 fields.  Tier 1 fields are defined in the RADx Global Code Book.")
public class DropTierOneFieldsCommand implements CliCommand {

    @Option(names = "--id-field",
            required = true,
            description = "The name of the field that corresponds to the Id field (or variable name field).  This is typically, 'Id'.")
    protected String idField;

    @Mixin
    protected IoMixin ioMixin;

    private final CliCsvTransformerProcessor processor;

    public DropTierOneFieldsCommand(CliCsvTransformerProcessor processor, GlobalCodeBookFactory globalCodeBookFactory) {
        this.processor = processor;
        this.globalCodeBookFactory = globalCodeBookFactory;
    }

    private final GlobalCodeBookFactory globalCodeBookFactory;

    @Override
    public Integer call() throws Exception {
        var transformer = new DropTierOneFieldsTransformer(idField, globalCodeBookFactory);
        processor.transformCsvFiles(ioMixin.in,
                                    ioMixin.out,
                                    transformer);
        return 0;
    }
}
