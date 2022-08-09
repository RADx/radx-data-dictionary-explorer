package edu.stanford.radx;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-19
 */
public class FilteringCsvTransformer implements CsvTransformer {

    private final Map<String, Pattern> filteringPatterns;

    /**
     * Create a filtering transformer from the specified pattern map.  The map maps field names to filtering patterns.
     */
    public FilteringCsvTransformer(Map<String, Pattern> filteringPatterns) {
        this.filteringPatterns = new HashMap<>(filteringPatterns);
    }

    @Override
    public List<Csv> transformCsv(Csv csv) {
        var fieldFilteringPatterns = csv.header()
                .stream()
                .map(filteringPatterns::get)
                .toList();

        var filtered = csv.content()
                .stream()
                .filter(row -> isIncluded(row, fieldFilteringPatterns))
                .toList();
        return List.of(new Csv(csv.coordinates(), csv.header(), filtered));
    }

    private boolean isIncluded(List<String> row, List<Pattern> patterns) {
        for (int i = 0; i < row.size(); i++) {
            if(i < patterns.size()) {
                var value = row.get(i);
                var pattern = patterns.get(i);
                if(pattern != null && !pattern.matcher(value).find()) {
                    return false;
                }
            }
        }
        return true;
    }
}
