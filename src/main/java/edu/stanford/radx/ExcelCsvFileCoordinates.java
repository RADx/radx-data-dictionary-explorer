package edu.stanford.radx;

import java.nio.file.Path;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-06-30
 */
public record ExcelCsvFileCoordinates(Path workbookPath, String sheetName) implements CsvCoordinates {

    @Override
    public Path getParentDirectory() {
        return workbookPath.getParent();
    }

    @Override
    public String getDescription() {
        return workbookPath.getFileName().toString() + " [" + sheetName + "]";
    }
}
