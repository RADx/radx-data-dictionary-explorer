package edu.stanford.radx.cli;

import com.google.common.base.Strings;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-19
 */
public class CsvConsolePrinter {


    private static final String ANSI_RESET = "\u001B[0m";

    private static final String ANSI_GRAY_BOLD = "\u001B[90;1m";

    private static final int MIN_COLUMN_WIDTH = 4;

    private final PrintStream out;

    public CsvConsolePrinter(PrintStream out) {
        this.out = out;
    }

    public void print(List<String> headerRow, List<List<String>> records) {

        var columnWidths = getColumnWidths(records, headerRow);

        var formatSpec = getColumnFormatSpec(columnWidths);
        out.print(ANSI_GRAY_BOLD);
        out.printf(formatSpec + "\n", headerRow.stream().map(header -> header.replace("\n", " ")).toArray());

        var rule = getColumnHeaderUnderline(columnWidths);
        out.printf("%s\n", rule);
        out.print(ANSI_RESET);

        records.stream()
                .map(values -> values.stream().map(val -> val.replace("\n", " ")))
                       .map(values -> String.format(formatSpec, values.toArray()))
                       .forEach(line -> out.printf("%s\n", line));
    }

    private static String getColumnFormatSpec(List<Integer> columnWidths) {
        return columnWidths.stream()
                           .map(minWidth -> "%-" + minWidth + "." + minWidth + "s")
                           .collect(Collectors.joining("    "));
    }

    private static String getColumnHeaderUnderline(List<Integer> columnWidths) {
        return columnWidths.stream()
                           .map(columnWidth -> Integer.max(columnWidth, MIN_COLUMN_WIDTH))
                           .map(minWidth -> Strings.padStart("", minWidth, '\u203E'))
                           .collect(Collectors.joining("    "));
    }

    private static List<Integer> getColumnWidths(List<List<String>> records, List<String> headerRow) {
        var columnWidths = new ArrayList<Integer>();
        for (int i = 0; i < headerRow.size(); i++) {
            final var index = i;
            final var headerRowWidth = headerRow.get(i).length();
            var columnMaxWidth = records.stream()
                                        .map(row -> row.get(index))
                                        .map(String::length)
                    .map(length -> Integer.max(length, headerRowWidth))
                                        .map(length -> Integer.max(length, MIN_COLUMN_WIDTH))
                                        .max(Integer::compare)
                                        .orElse(0);
            columnWidths.add(columnMaxWidth);
        }
        return columnWidths;
    }
}
