package edu.stanford.radx;

import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-01
 */
public interface CsvSummaryWriter {

    interface CsvWriter {
        void writeRecord(List<?> values);
    }

    String getFileName();

    void writeSummary(List<CsvFieldDescriptor> fieldDescriptors, CsvWriter writer);
}
