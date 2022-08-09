package edu.stanford.radx;

import java.nio.file.Path;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-06-30
 */
public record ExcelCsvFileContents(Path sourceFile, String sheetName, String csvContents) {

    public CsvFileContents getCsvFileContents() {
        return new CsvFileContents(new ExcelCsvFileCoordinates(sourceFile, sheetName), csvContents);
    }
}
