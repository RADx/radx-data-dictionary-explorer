package edu.stanford.radx.cli;

import edu.stanford.radx.Csv;
import edu.stanford.radx.CsvCoordinates;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-22
 */
public class CliCsvWriter {


    public CliCsvWriter() {
    }

    private String getOutputFileName(CsvCoordinates coordinates) {
        var desc = coordinates.getDescription();
        if(desc.endsWith(".csv")) {
            return desc;
        }
        else {
            return desc + ".csv";
        }
    }

    public void writeCsv(Path out, Csv csv) {
        try {
            var buffer = new StringBuilder();
            var printer = new CSVPrinter(buffer, CSVFormat.DEFAULT);
            printer.printRecord(csv.header());

            csv.content()
                    .forEach(row -> {
                        try {
                            printer.printRecord(row);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
            var outputFileName = getOutputFileName(csv.coordinates());
            var outputDirectoryName = csv.coordinates().getParentDirectory().getFileName().toString();
            var outputPath = out.resolve(outputDirectoryName).resolve(outputFileName);
            if (!Files.exists(outputPath.getParent())) {
                Files.createDirectories(outputPath.getParent());
            }
            Files.writeString(outputPath, buffer.toString());
        } catch (IOException e) {
            System.err.println("An error occurred when writing out the CSV files: " + e.getMessage());
        }

    }
}
