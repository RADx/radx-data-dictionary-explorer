package edu.stanford.radx.valueconstraints;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-23
 */
public class RedCapChoiceListParser {

    private static final String VALUE_REGEX = "(-?\\d+)\\s*,\\s*\"?(.+)\"?";

    private static final Pattern valuePattern = Pattern.compile(VALUE_REGEX);

    private static final String CHOICE_DELIMETER_REGEX = "[;|]";

    public static boolean isChoicesList(String specification) {
        return Arrays.stream(specification.split(CHOICE_DELIMETER_REGEX))
                     .map(String::trim)
                     .allMatch(element -> element.matches(VALUE_REGEX));
    }

    public List<RedCapChoice> getChoices(String specification) {
        return Arrays.stream(specification.split(CHOICE_DELIMETER_REGEX))
                     .map(String::trim)
                     .map(valuePattern::matcher)
                     .filter(Matcher::matches)
                     .map(matcher -> new RedCapChoice(matcher.group(1), matcher.group(2)))
                     .collect(Collectors.toList());
    }
}
