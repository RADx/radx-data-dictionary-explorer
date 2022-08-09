package edu.stanford.radx;

import java.util.regex.Pattern;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-05
 */
public class FormulaHyperlinkResolver {

    private static final Pattern hyperlinkFriendlyName = Pattern.compile("\"([^\"]+)\"\\)$");

    public String getResolvedFieldName(String fieldName) {
        if(fieldName.toLowerCase().startsWith("=hyperlink(")) {
            var matcher = hyperlinkFriendlyName.matcher(fieldName);
            var friendlyName = "";
            while(matcher.find()) {
                friendlyName = matcher.group(1);
            }
            if(!friendlyName.equals("")) {
                return friendlyName;
            }
        }
        return fieldName;
    }
}
