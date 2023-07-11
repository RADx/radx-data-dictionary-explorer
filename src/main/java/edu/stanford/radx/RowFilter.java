package edu.stanford.radx;

import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-12-01
 */
public class RowFilter {

    private final List<FilterSpecification> filterSpecifications;

    public RowFilter(List<FilterSpecification> filterSpecifications) {
        this.filterSpecifications = filterSpecifications;
    }

    public boolean isMatched(Csv csv, List<String> row) {
        for(var filterSpecification : filterSpecifications) {
            var fieldName = filterSpecification.fieldName();
            int index = csv.getIndex(fieldName);
            if(index != -1 && index < row.size()) {
                var rowValue = row.get(index);
                var matcher = filterSpecification.pattern().matcher(rowValue);
                if(!matcher.find()) {
                    return false;
                }
            }
        }
        return true;
    }
}
