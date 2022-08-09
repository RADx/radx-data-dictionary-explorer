package edu.stanford.radx.cli;

import picocli.CommandLine;

import java.nio.file.Path;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-26
 */
public class IoMixin {


    @CommandLine.Option(names = "--in",
            required = true,
            description = "The path to the input files.  This can either be a single file, a list of files, a single directory or a list of directories.  Multiple entries should be separated by commas.")
    public List<Path> in;

    @CommandLine.Option(names = "--out",
            required = true,
            description = "The path where CSV files are output to.  This must be a directory.")
    public Path out;

}
