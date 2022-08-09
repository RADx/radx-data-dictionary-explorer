package edu.stanford.radx;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-21
 */
public record Csv(CsvCoordinates coordinates,
                  List<String> header,
                  List<List<String>> content) {

    public Csv(CsvCoordinates coordinates, List<String> header, List<List<String>> content) {
        this.coordinates = coordinates;
        this.header = List.copyOf(header);
        this.content = List.copyOf(content);
    }

    public Csv withSource() {
        return new Csv(coordinates(),
                       headerWithSource(),
                       contentWithSource());
    }

    public Csv padShortRowsToHeader() {
        var paddedRows = content.stream()
                .map(row -> {
                    if(row.size() < header.size()) {
                        var paddedRow = new ArrayList<>(row);
                        for(int i = header.size() - row.size(); i < header.size(); i++) {
                            paddedRow.add("");
                        }
                        return paddedRow;
                    }
                    else {
                        return row;
                    }
                })
                .toList();
        return new Csv(coordinates, header, paddedRows);
    }

    public Csv withoutEmptyColumns() {
        var nonEmptyFlags = new ArrayList<Boolean>();
        var nonEmptyHeader = new ArrayList<String>();
        for(int i = 0; i < header.size(); i++) {
            var columnIndex = i;
            var nonEmpty = content.stream()
                    .filter(row -> columnIndex < row.size())
                                  .map(row -> row.get(columnIndex))
                    .anyMatch(cell -> !cell.isBlank());
            nonEmptyFlags.add(nonEmpty);
            if(nonEmpty) {
                nonEmptyHeader.add(header.get(columnIndex));
            }
        }
        var nonEmptyContent = content().stream()
                .map(row -> {
                    var nonEmptyRow = (List<String>) new ArrayList<String>();
                    for(int i = 0; i < header.size(); i++) {
                        if (i < row.size()) {
                            var nonEmpty = nonEmptyFlags.get(i);
                            var nonEmptyVal = row.get(i);
                            if (nonEmpty) {
                                nonEmptyRow.add(nonEmptyVal);
                            }
                        }
                    }
                    return nonEmptyRow;
                }).toList();
        return new Csv(coordinates, nonEmptyHeader, nonEmptyContent);

    }

    private List<String> headerWithSource() {
        var hdr = new ArrayList<>(header);
        hdr.add("source_file");
        hdr.add("source_directory");
        return hdr;
    }

    private List<List<String>> contentWithSource() {
        return content.stream()
                .map(row -> {
                    List<String> r = new ArrayList<>(row);
                    r.add(coordinates.getDescription());
                    r.add(coordinates.getParentDirectory().getFileName().toString());
                    return r;
                })
                .toList();
    }

    public int getIndex(String columnName) {
        for(int i = 0; i < header.size(); i++) {
            var colHeader = header.get(i);
            if(colHeader.trim().equals(columnName.trim())) {
                return i;
            }
        }
        return -1;
    }


    public Csv withoutEmptyRows() {
        var nonEmptyRows = content.stream()
                .filter(row -> row.stream().anyMatch(not(String::isBlank)))
                .collect(Collectors.toList());
        return new Csv(coordinates, header, nonEmptyRows);
    }
}
