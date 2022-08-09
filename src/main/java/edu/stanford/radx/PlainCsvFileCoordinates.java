package edu.stanford.radx;

import java.nio.file.Path;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-06-30
 */
public record PlainCsvFileCoordinates(Path filePath) implements CsvCoordinates {

    @Override
    public Path getParentDirectory() {
        return filePath.getParent();
    }

    @Override
    public String getDescription() {
        return filePath.getFileName().toString();
    }
}
