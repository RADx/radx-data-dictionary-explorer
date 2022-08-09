package edu.stanford.radx;

import java.nio.file.Path;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-06-30
 */
public interface CsvCoordinates extends Comparable<CsvCoordinates> {

    /**
     * Gets a human readable description of the coordinates of a CSV file
     */
    String getDescription();

    Path getParentDirectory();

    @Override
    default int compareTo(CsvCoordinates o) {
        return this.getDescription().compareToIgnoreCase(o.getDescription());
    }
}
