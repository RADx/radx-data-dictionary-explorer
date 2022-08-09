package edu.stanford.radx;

import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-19
 */
public interface CsvTransformer {

    /**
     * Transform the specified CSV.
     * @param csv The csv as a list of lists.  The number of columns in the returned CSV will be the same (with the
     *            same header rows)
     */
    List<Csv> transformCsv(Csv csv);
}
