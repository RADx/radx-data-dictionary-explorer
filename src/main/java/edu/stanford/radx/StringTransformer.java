package edu.stanford.radx;

import java.util.List;

import static java.util.function.Predicate.not;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-25
 */
public class StringTransformer implements CsvTransformer {

    private final List<String> fieldNames;

    private final List<StringTransform> transforms;

    public StringTransformer(List<String> fieldNames, List<StringTransform> transforms) {
        this.fieldNames = fieldNames;
        this.transforms = transforms;
    }

    @Override
    public List<Csv> transformCsv(Csv csv) {
        var transformIndexes = fieldNames.stream()
                                     .filter(not(String::isEmpty))
                                     .map(csv::getIndex)
                                     .toList();
        var collapseWhiteSpace = transforms.contains(StringTransform.COLLAPSE_WHITE_SPACE);
        var lowercase = transforms.contains(StringTransform.TO_LOWER_CASE);
        var uppercase = transforms.contains(StringTransform.TO_UPPER_CASE);
        var transformer = new CsvStreamTransformer(transformIndexes, false, collapseWhiteSpace, lowercase, false,
                                                   uppercase);
        var transformed = transformer.transform(csv.content().stream())
                                     .toList();
        return List.of(new Csv(csv.coordinates(),
                               csv.header(),
                               transformed));
    }
}
