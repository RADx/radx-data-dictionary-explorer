package edu.stanford.radx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-05
 */
public class FieldNameSynonyms {

    private final Map<String, String> map = new HashMap<>();

    private final FieldNameNormalizer fieldNameNormalizer;

    public FieldNameSynonyms(FieldNameNormalizer fieldNameNormalizer) {
        this.fieldNameNormalizer = fieldNameNormalizer;
    }

    public void load(Map<String, String> map) {
        this.map.clear();
        map.forEach((k, v) -> {
            var normalizedFieldName = fieldNameNormalizer.normalizeFieldName(k);
            this.map.put(normalizedFieldName, v);
            if (!this.map.containsKey(v)) {
                this.map.put(v, v);
            }
        });
    }

    public Map<String, String> getCanonicalFieldNamesMap() {
        return Map.copyOf(map);
    }

    /**
     * Gets the canonical name for the specified field name
     * @param fieldName The field name.
     * @return The canonical name for the specified field name.  If one does not exist then the empty string is returned.
     */
    public String getCanonicalFieldName(String fieldName) {
        var normalized = fieldNameNormalizer.normalizeFieldName(fieldName);
        if(map.isEmpty()) {
            return fieldName;
        }
        var cfn = map.get(normalized);
        if(cfn != null) {
            return cfn;
        }
        return fieldName;
    }

    public int getFieldNameIndex(String canonicalFieldName, List<String> fieldNames) {
        for(int i = 0; i < fieldNames.size(); i++) {
            var canonName = getCanonicalFieldName(fieldNames.get(i));
            if(canonName.equals(canonicalFieldName)) {
                return i;
            }
        }
        return -1;
    }
}
