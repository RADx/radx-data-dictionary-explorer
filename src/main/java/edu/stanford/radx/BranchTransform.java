package edu.stanford.radx;

import edu.stanford.radx.cli.FilterMixin;
import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.util.ArrayList;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-12-08
 */
public class BranchTransform implements CsvTransformer {

    private final List<FilterSpecification> filters;

    private final String branchField;

    public BranchTransform(List<FilterSpecification> filters, String branchField) {
        this.filters = new ArrayList<>(filters);
        this.branchField = branchField;
    }

    @Override
    public List<Csv> transformCsv(Csv csv) {
        var result = new ArrayList<Csv>();
        var rowFilter = new RowFilter(filters);
        var matchedRows = csv.content()
                .stream()
                .filter(row -> rowFilter.isMatched(csv, row))
                .toList();
        if(matchedRows.isEmpty()) {
            return List.of(csv);
        }
        for(var branchingRow : matchedRows) {
            var branchedContent = csv.content()
                    .stream()
                    .filter(row -> !matchedRows.contains(row) || row.equals(branchingRow))
                    .toList();
            var branchNameIndex = csv.getIndex(branchField);
            if(branchNameIndex != -1 && branchNameIndex < branchingRow.size()) {
                var branchName = branchingRow.get(branchNameIndex);
                var branchFileName = (branchName.isBlank() ? csv.coordinates().getDescription() : branchName);
                var branchedCsvCoordinates = new PlainCsvFileCoordinates(csv.coordinates().getParentDirectory().resolve(branchFileName));
                var branchedCsv = new Csv(branchedCsvCoordinates, csv.header(), branchedContent);
                result.add(branchedCsv);
            }
        }
        return result;
    }
}
