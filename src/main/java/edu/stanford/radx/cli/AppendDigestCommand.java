package edu.stanford.radx.cli;

import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.springframework.stereotype.Component;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-08-09
 */
@Component
@Command(name = "append-digest", description = "Creates a digest of a sequence of field values.  Missing values are digested as the empty string.")
public class AppendDigestCommand implements CliCommand{

    @Mixin
    private IoMixin io;

    @CommandLine.Option(names = "--field-names", split = ",", required = true, description = "A comma separated list of field names from which to create the digest.")
    private List<String> fieldNames = new ArrayList<>();

    private final CliCsvTransformerProcessor processor;

    public AppendDigestCommand(CliCsvTransformerProcessor processor) {
        this.processor = processor;
    }

    @Override
    public Integer call() throws Exception {
        processor.transformCsvFiles(io.in, io.out, new MessageDigestAppenderTransformer(fieldNames));
        return 0;
    }
}
