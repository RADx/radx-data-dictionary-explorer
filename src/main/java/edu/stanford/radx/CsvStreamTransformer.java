package edu.stanford.radx;

import com.google.common.collect.Ordering;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-20
 */
public class CsvStreamTransformer {

    private final List<Integer> transformIndexes;

    private final boolean distinct;

    private final boolean collapseWhiteSpace;

    private final boolean lowercase;

    private final boolean sorted;

    private final boolean uppercase;

    public CsvStreamTransformer(List<Integer> transformIndexes,
                                boolean distinct,
                                boolean collapseWhiteSpace,
                                boolean lowercase,
                                boolean sorted,
                                boolean uppercase) {
        this.transformIndexes = transformIndexes;
        this.distinct = distinct;
        this.collapseWhiteSpace = collapseWhiteSpace;
        this.lowercase = lowercase;
        this.uppercase = uppercase;
        this.sorted = sorted;
    }

    public Stream<List<String>> transform(Stream<List<String>> in) {
        // Order of operations is important.  Lowercase then eliminate duplicates then sort
        return sortedIfNecessary(distinctIfNecessary(collapseWhiteSpace(lowerCaseIfNecessary(upperCaseIfNecessary(in)))));
    }

    private <T> Stream<T> distinctIfNecessary(Stream<T> stream) {
        if(distinct) {
            return stream.distinct();
        }
        else {
            return stream;
        }
    }

    private Stream<List<String>> collapseWhiteSpace(Stream<List<String>> in) {
        if (collapseWhiteSpace) {
            return in.map(row -> transformRow(row, s -> s.replace('\n', ' ').replaceAll("\\s+", " ").trim()));
        }
        else {
            return in;
        }
    }

    private List<String> transformRow(List<String> row, Function<String, String> transform) {
        if(transformIndexes.isEmpty()) {
            return row.stream().map(transform).toList();
        }
        else {
            var transformedRow = new ArrayList<>(row);
            for(var indexToTransform : transformIndexes) {
                if (indexToTransform >= 0 && indexToTransform < row.size()) {
                    var existingValue = row.get(indexToTransform);
                    transformedRow.set(indexToTransform, transform.apply(existingValue));
                }
            }
            return transformedRow;
        }
    }

    private Stream<List<String>> lowerCaseIfNecessary(Stream<List<String>> in) {
        if (lowercase) {
            return in.map(row -> transformRow(row, String::toLowerCase));
        }
        else {
            return in;
        }
    }

    private Stream<List<String>> upperCaseIfNecessary(Stream<List<String>> in) {
        if (uppercase) {
            return in.map(row -> transformRow(row, String::toUpperCase));
        }
        else {
            return in;
        }
    }

    private Stream<List<String>> sortedIfNecessary(Stream<List<String>> in) {
        if (sorted) {
            return in.sorted(Ordering.from(String::compareToIgnoreCase).lexicographical());
        }
        else {
            return in;
        }
    }

}
