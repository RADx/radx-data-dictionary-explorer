package edu.stanford.radx;

import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-01
 */
public class FieldsSourcesValuesSummaryWriter implements CsvSummaryWriter {

    @Override
    public String getFileName() {
        return "field-names-sources-values.csv";
    }

    @Override
    public void writeSummary(List<CsvFieldDescriptor> fieldDescriptors, CsvWriter writer) {
        writer.writeRecord(List.of("Field Name", "Data Dictionary", "Sample Values"));
        fieldDescriptors.stream()
                .sorted()
                        .flatMap(descriptor -> descriptor.exampleContent()
                                                         .stream()
                                                         .map(ex -> List.of(descriptor.fieldName(),
                                                                            descriptor.source().getDescription(),
                                                                            ex)))
                        .forEach(writer::writeRecord);
    }
}
