package edu.stanford.radx;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-06-30
 */
public class Excel2Csv {

    private static final Logger logger = LoggerFactory.getLogger(Excel2Csv.class);

    private static final String BLANK_CELL_VALUE = "";

    private final ExcelWorkbookParser workbookParser;

    public Excel2Csv(ExcelWorkbookParser workbookParser) {
        this.workbookParser = workbookParser;
    }

    public List<ExcelCsvFileContents> extractCsvFiles(Path excelSourceFile) {
        var workbook = workbookParser.parseWorkbook(excelSourceFile);
        return workbook.map(wb -> extractCsvContents(excelSourceFile, wb))
                       .orElse(Collections.emptyList());
    }

    private List<ExcelCsvFileContents> extractCsvContents(Path sourcePath, Workbook workbook) {
            var evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            var sheetsAsCsvs = new ArrayList<ExcelCsvFileContents>();
            try {
                for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                    var sheet = workbook.getSheetAt(sheetIndex);
                    var sheetName = sheet.getSheetName();
                    if (!SheetNameStopList.isStopped(sheetName)) {
                        var csvContents = extractCsvContentsFromSheet(sourcePath, workbook, evaluator,  sheetIndex);
                        sheetsAsCsvs.add(csvContents);
                    }
                }
            } catch (IOException e) {
                logger.warn("Could not parse sheet in {}", sourcePath);
            }
            return sheetsAsCsvs;
    }

    private static ExcelCsvFileContents extractCsvContentsFromSheet(Path sourcePath,
                                                             Workbook workbook,
                                                             FormulaEvaluator evaluator,
                                                             int sheetIndex) throws IOException {
        var sheet = workbook.getSheetAt(sheetIndex);
        var stringBuilder = new StringBuilder();
        var csvPrinter = new CSVPrinter(stringBuilder, CSVFormat.DEFAULT);
        for (int rowIndex = 0; rowIndex < sheet.getLastRowNum(); rowIndex++) {
            var row = sheet.getRow(rowIndex);
            if (row != null) {
                var values = getRowValues(row, evaluator);
                csvPrinter.printRecord(values);
            }
        }
        var csvContents = stringBuilder.toString();
        return new ExcelCsvFileContents(sourcePath, sheet.getSheetName(), csvContents);
    }

    private static List<Object> getRowValues(Row row, FormulaEvaluator evaluator) {
        var values = new ArrayList<>();
        for (int i = 0; i < row.getLastCellNum(); i++) {
            var cell = row.getCell(i);
            if (cell != null) {
                var value = extractCellValue(cell, evaluator);
                values.add(value);
            }
            else {
                values.add(BLANK_CELL_VALUE);
            }
        }
        var nonBlankCellValuesCount = values.stream().filter(v -> !v.equals(BLANK_CELL_VALUE))
                .count();
        if(nonBlankCellValuesCount != 0) {
            return values;
        }
        else {
            return Collections.emptyList();
        }
    }

    private static String extractCellValue(Cell cell, FormulaEvaluator evaluator) {
        try {
            var cellValue = evaluator.evaluate(cell);
            if(cellValue == null) {
                return "";
            }
            var cellType = cellValue.getCellType();
            return switch (cellType) {
                case STRING -> cellValue.getStringValue();
                case NUMERIC -> Double.toString(cellValue.getErrorValue());
                case BOOLEAN -> Boolean.toString(cellValue.getBooleanValue());
                case ERROR, _NONE, BLANK -> BLANK_CELL_VALUE;
                // Formula will never occur if we evaluate it... according to the docs
                case FORMULA -> BLANK_CELL_VALUE;
            };
        } catch (Exception e) {
            logger.warn("Error evaluating cell: {} ({}:{}) – {}", cell.getSheet().getSheetName(), Character.toString(cell.getColumnIndex() + 'A'), cell.getRowIndex() + 1, e.getMessage());
            return "";
        }
    }
}
