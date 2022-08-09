package edu.stanford.radx;

import java.util.List;

import static java.util.function.Predicate.not;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-12
 */
public class DataDictionaryFilter {

    private final FieldNameSynonyms fieldNameSynonyms;

    public DataDictionaryFilter(FieldNameSynonyms fieldNameSynonyms) {
        this.fieldNameSynonyms = fieldNameSynonyms;
    }


    public boolean isLikelyDataDictionary(List<List<String>> records) {
        if(records.isEmpty()) {
            return false;
        }

        var headerRow = records.get(0);
        var canonicalHeaders = headerRow
                .stream()
                .map(fieldNameSynonyms::getCanonicalFieldName)
                .filter(not(String::isBlank))
                .count();
        return canonicalHeaders >= 1;
    }
}
