package edu.stanford.radx;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-06-30
 */
public class ExcelWorkbookParser {

    private static final Logger logger = LoggerFactory.getLogger(ExcelWorkbookParser.class);

    public Optional<Workbook> parseWorkbook(Path workbookPath) {
        try {
            return Optional.of(new XSSFWorkbook(Files.newInputStream(workbookPath)));
        } catch (Exception e) {
            try {
                logger.warn("Could not parse input file {} as Excel XSSF Workbook.  Attempting to parse as HSSF Workbook", workbookPath, e);
                return Optional.of(new HSSFWorkbook(Files.newInputStream(workbookPath)));
            } catch (Exception ex) {
                logger.warn("Could not parse input file {} as Excel HSSF Workbook.", workbookPath, e);
                return Optional.empty();
            }
        }
    }
}
