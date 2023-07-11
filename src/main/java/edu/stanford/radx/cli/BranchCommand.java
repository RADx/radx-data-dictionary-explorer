package edu.stanford.radx.cli;

import edu.stanford.radx.BranchTransform;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-12-08
 */
@Component
@Command(name = "branch", description = "Branches a CSV file into multiple CSV files based on a subset of rows.  One CSV file per row in the subset will be generated.  Branching works as follows. Let rows(csv) be the rows in a csv file.  Let branches(csv) be a subset of rows(csv).  For each branchingRow in branches(csv) generate a CSV containing rows(csv) - branches(csv) + branchingRow.")
public class BranchCommand implements CliCommand {

    @Mixin
    private IoMixin io;

    @Mixin
    private FilterMixin filter;

    @CommandLine.Option(names = "--branch-name-field", description = "The name of the field (column) from which to derive the branch name")
    private String branchField;


    private final CliCsvTransformerProcessor processor;

    public BranchCommand(CliCsvTransformerProcessor processor) {
        this.processor = processor;
    }

    @Override
    public Integer call() throws Exception {
        var transform = new BranchTransform(filter.getFilters(), branchField);
        processor.transformCsvFiles(io.in, io.out, transform);
        return 0;
    }
}
