package edu.stanford.radx;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-07-10
 */
@Component
public class GlobalCodeBookParser {

    public List<GlobalCodeBookRecord> parse(InputStream inputStream) throws IOException {
        var csvMapper = new CsvMapper();
        var headerSchema = CsvSchema.emptySchema().withHeader();
        MappingIterator<GlobalCodeBookRecord> it = csvMapper
                .readerFor(GlobalCodeBookRecord.class)
                .with(headerSchema)
                .readValues(inputStream);
        return it.readAll();
    }
}
