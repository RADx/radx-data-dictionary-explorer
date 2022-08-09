package edu.stanford.radx;

import java.util.Comparator;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-06-30
 */
public record CsvFileContents(CsvCoordinates coordinates, String csvContent) implements Comparable<CsvFileContents> {

    private static final Comparator<CsvFileContents> comparator = getCsvFileContentsComparator();

    @Override
    public int compareTo(CsvFileContents o) {
        return comparator.compare(this, o);
    }

    private static Comparator<CsvFileContents> getCsvFileContentsComparator() {
        return Comparator.comparing(CsvFileContents::coordinates)
                         .thenComparing(CsvFileContents::csvContent);
    }
}
