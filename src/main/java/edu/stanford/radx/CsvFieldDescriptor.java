package edu.stanford.radx;

import com.google.common.collect.Ordering;

import java.util.Comparator;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-06-28
 */
public record CsvFieldDescriptor(CsvCoordinates source, int columnIndex, String fieldName, List<String> exampleContent) implements Comparable<CsvFieldDescriptor> {

    private static final Ordering<Iterable<String>> examplesComparator = Ordering.<String>natural().lexicographical();

    private static final Comparator<CsvFieldDescriptor> comparator =
            Comparator.comparing(CsvFieldDescriptor::fieldName, String::compareToIgnoreCase)
            .thenComparing(CsvFieldDescriptor::source)
            .thenComparing(CsvFieldDescriptor::exampleContent, examplesComparator);

    @Override
    public int compareTo(CsvFieldDescriptor o) {
        return comparator.compare(this, o);
    }
}
