package edu.stanford.radx;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-07-10
 */
public class GlobalCodeBookDccParser<R> {

    private final Class<R> cls;

    public GlobalCodeBookDccParser(Class<R> cls) {
        this.cls = cls;
    }

    public static <R> GlobalCodeBookDccParser<R> get(Class<R> cls) {
        return new GlobalCodeBookDccParser<R>(cls);
    }

    public List<R> parse(InputStream inputStream) throws IOException {
        var csvMapper = new CsvMapper();
        var headerSchema = CsvSchema.emptySchema().withHeader();
        MappingIterator<R> it = csvMapper
                .readerFor(cls)
                .with(headerSchema)
                .readValues(inputStream);
        return it.readAll();
    }

}
