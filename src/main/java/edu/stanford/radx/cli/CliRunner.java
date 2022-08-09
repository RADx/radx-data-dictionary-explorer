package edu.stanford.radx.cli;

import org.springframework.stereotype.Component;
import picocli.CommandLine;

import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-26
 */
@Component
public class CliRunner {

    private final CommandLine.IFactory factory;

    private final List<CliCommand> commandList;

    public CliRunner(CommandLine.IFactory factory, List<CliCommand> commandList) {
        this.factory = factory;
        this.commandList = commandList;
    }

    public Integer execute(String [] args) throws Exception {
        var cli = new CommandLine(new DataDictionaryCommand(), factory);
        commandList.forEach(cli::addSubcommand);
        return cli.execute(args);
    }
}
