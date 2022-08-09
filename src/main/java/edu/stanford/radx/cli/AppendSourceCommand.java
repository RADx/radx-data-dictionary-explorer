package edu.stanford.radx.cli;

import org.springframework.stereotype.Component;
import picocli.CommandLine.Mixin;

import java.util.List;

import static picocli.CommandLine.Command;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-25
 */
@Component
@Command(name = "append-source",
        description = "Appends the source file name and parent directory as two columns to the CSV.  Each line in the resulting CSV file will have a source_file and source_directory column added to it.")
public class AppendSourceCommand implements CliCommand {

    @Mixin
    IoMixin io;

    private final CliCsvTransformerProcessor processor;

    public AppendSourceCommand(CliCsvTransformerProcessor processor) {
        this.processor = processor;
    }

    @Override
    public Integer call() throws Exception {
        processor.transformCsvFiles(io.in, io.out, csv -> List.of(csv.padShortRowsToHeader().withSource()));
        return 0;
    }

}
