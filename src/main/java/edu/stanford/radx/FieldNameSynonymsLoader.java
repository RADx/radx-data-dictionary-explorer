package edu.stanford.radx;

import com.google.common.base.Charsets;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-01
 */
public class FieldNameSynonymsLoader {

    private static final Logger logger = LoggerFactory.getLogger(FieldNameSynonymsLoader.class);

    private final FieldNameSynonyms fieldNameSynonyms;

    public FieldNameSynonymsLoader(FieldNameSynonyms fieldNameSynonyms) {
        this.fieldNameSynonyms = fieldNameSynonyms;
    }

    public Map<String, String> load(@Nullable Path fieldNamesMapPath) throws IOException {
        var map = loadMap(fieldNamesMapPath);
        fieldNameSynonyms.load(map);
        return map;
    }


    private InputStream getFieldNameMapInputStream(@Nullable Path fieldNamesMap) throws IOException {
        if(fieldNamesMap == null) {
            return FieldNameSynonymsLoader.class.getResourceAsStream("/field-name-synonyms.csv");
        }
        else {
            return Files.newInputStream(fieldNamesMap);
        }
    }

    private Map<String, String> loadMap(@Nullable Path fieldNamesMapPath) {
        try {
            var inputStream = getFieldNameMapInputStream(fieldNamesMapPath);
            var result = new HashMap<String, String>();
            var csvParser = new CSVParser(new InputStreamReader(inputStream, Charsets.UTF_8), CSVFormat.DEFAULT);
            csvParser.stream()
                    .skip(1)
                    .forEach(record -> {
                        var from = record.get(0);
                        var to = record.get(1);
                        if (!to.isBlank()) {
                            result.put(from, to);
                            logger.info("Mapping: {} --> {}", from, to);
                        }
                    });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }
}
