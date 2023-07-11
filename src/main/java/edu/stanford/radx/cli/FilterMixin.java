package edu.stanford.radx.cli;

import edu.stanford.radx.FilterSpecification;
import picocli.CommandLine.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-12-01
 */
public class FilterMixin {

    @Option(names = "--filter", description = "--Specifies a filter that matches rows.  Filters are specified using the syntax fieldName=pattern where fieldName is the name of a field and patter in a regular expression.  Multiple filters may be specified by separating filters with commas.  For example, field1=regex1,field2=regex2.", split = ",")
    List<String> filters = new ArrayList<>();

    public List<FilterSpecification> getFilters() {
        return filters.stream()
                .filter(f -> f.indexOf('=') != -1 && f.indexOf('=') < f.length())
                .map(f -> new FilterSpecification(f.substring(0, f.indexOf('=')),
                                                  Pattern.compile(f.substring(f.indexOf('=') + 1))))
                .collect(Collectors.toList());
    }
}
