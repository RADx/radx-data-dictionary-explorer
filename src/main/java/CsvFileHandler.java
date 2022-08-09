import edu.stanford.radx.CsvCoordinates;

import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-21
 */
public interface CsvFileHandler {

    void handleCsvFile(CsvCoordinates coordinates, List<String> header, List<List<String>> records);
}
