package edu.stanford.radx;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-12
 */
public class FieldNameNormalizer {

    public FieldNameNormalizer() {

    }

    public String normalizeFieldName(String key) {
        var builder = new StringBuilder();
        for(int i = 0; i < key.length(); i++) {
            var ch = key.charAt(i);
            if(Character.isLetterOrDigit(ch)) {
                var lowerCase = Character.toLowerCase(ch);
                builder.append(lowerCase);
            }
        }
        return builder.toString();
    }
}
