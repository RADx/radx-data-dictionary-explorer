package edu.stanford.radx.cli;

import edu.stanford.radx.GlobalCodeBookFactory;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-07-11
 */
@Component
@Command(name = "append-global-code-book")
public class AppendGlobalCodeBookCommand implements CliCommand {

    private final GlobalCodeBookFactory globalCodeBookFactory;

    private final CliCsvTransformerProcessor processor;

    @Mixin
    protected IoMixin ioMixin;

    public AppendGlobalCodeBookCommand(GlobalCodeBookFactory globalCodeBookFactory,
                                       CliCsvTransformerProcessor processor) {
        this.globalCodeBookFactory = globalCodeBookFactory;
        this.processor = processor;
    }

    @Override
    public Integer call() throws Exception {
        processor.transformCsvFiles(ioMixin.in,
                                           ioMixin.out,
                                           new AppendGlobalCodeBookTransformer(globalCodeBookFactory));
        return 0;
    }
}
