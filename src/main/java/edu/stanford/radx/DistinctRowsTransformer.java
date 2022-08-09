package edu.stanford.radx;

import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-25
 */
public class DistinctRowsTransformer implements CsvTransformer {

    @Override
    public List<Csv> transformCsv(Csv csv) {
        var distinctRows = csv.content()
                              .stream()
                              .distinct()
                              .toList();
        return List.of(new Csv(csv.coordinates(), csv.header(), distinctRows));
    }
}
