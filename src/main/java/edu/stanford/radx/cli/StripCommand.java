package edu.stanford.radx.cli;

import org.springframework.stereotype.Component;

import java.util.List;

import static picocli.CommandLine.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-22
 */
@Component
@Command(name = "strip", description = "Strips out empty columns and/or rows")
public class StripCommand implements CliCommand {

    @Mixin
    IoMixin io;

    @Option(names = "--strip-empty-columns", defaultValue = "true", description = "Empty columns will be removed.  This is true by default.")
    public boolean stripEmptyColumns = true;

    @Option(names = "--strip-empty-rows", defaultValue = "true", description = "Empty rows will be removed.  This is true by default")
    public boolean stripEmptyRows = true;

    private final CliCsvTransformerProcessor processor;

    public StripCommand(CliCsvTransformerProcessor processor) {
        this.processor = processor;
    }

    @Override
    public Integer call() throws Exception {
        processor.transformCsvFiles(io.in, io.out, csv -> {
            if(stripEmptyRows) {
                csv = csv.withoutEmptyRows();
            }
            if(stripEmptyColumns) {
                csv = csv.withoutEmptyColumns();
            }
            return List.of(csv);
        });
        return 0;
    }
}
