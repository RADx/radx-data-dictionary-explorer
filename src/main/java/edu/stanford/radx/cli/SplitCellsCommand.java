package edu.stanford.radx.cli;

import edu.stanford.radx.RowFilter;
import edu.stanford.radx.SplitCellsTransformer;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-12-01
 */
@Component
@Command(name = "split-cells", description = "Split the cells in a field by splitting cell content on a delimeter and then inserting one row per split value with other cells in the row duplicated.")
public class SplitCellsCommand implements CliCommand {

    @Mixin
    private IoMixin ioMixin;

    @Mixin
    private FilterMixin filterMixin;

    @Option(names = "--field-name")
    String fieldName;

    @Option(names = "--delimiters", defaultValue = ",;|")
    String delimiters = ",;|\\n ";

    private final CliCsvTransformerProcessor transformerProcessor;

    public SplitCellsCommand(CliCsvTransformerProcessor transformerProcessor) {
        this.transformerProcessor = transformerProcessor;
    }

    @Override
    public Integer call() throws Exception {
        var rowFilter = new RowFilter(filterMixin.getFilters());
        var transformer = new SplitCellsTransformer(fieldName, delimiters, rowFilter);
        transformerProcessor.transformCsvFiles(ioMixin.in,
                                               ioMixin.out,
                                               transformer);
        return 0;
    }
}
