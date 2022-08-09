package edu.stanford.radx.valueconstraints;

import java.util.regex.Pattern;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-23
 */
public enum ValueConstraintPatterns {

    REDCAP_FORMATTED_CHOICE(Pattern.compile(""));

    ValueConstraintPatterns(Pattern pattern) {
        this.pattern = pattern;
    }

    private final Pattern pattern;
}
