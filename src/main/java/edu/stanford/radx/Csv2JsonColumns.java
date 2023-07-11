package edu.stanford.radx;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Streams;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Stream;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-11-30
 */
@Component
public class Csv2JsonColumns {

    public Map<String, Object> convertToJson(Csv csv,
                                                         String groupingKey,
                                                         Set<String> multiValuedKeys,
                                                         Set<String> splitValueKeys,
                                                         Set<String> excludedKeys) {
        var rowsByKey = LinkedHashMultimap.<String, Object>create();
        csv.content()
                .forEach(row -> {
                    var key = row.get(0);
                    if (!excludedKeys.contains(key)) {
                        var values = new LinkedHashMap<String, Object>();
                        int counter = 0;
                        for(var headerName : csv.header()) {
                            if (!headerName.isBlank() && !groupingKey.equals(headerName)) {
                                if (counter < row.size()) {
                                    var rawValue = row.get(counter);
                                    if (!rawValue.isBlank()) {
                                        if (splitValueKeys.contains(key)) {
                                            var splitValues = Stream.of(rawValue.split("[,;|\n]")).map(String::trim).toList();
                                            values.put(headerName, splitValues);
                                        }
                                        else {
                                            values.put(headerName, rawValue.trim());
                                        }
                                    }
                                }
                            }
                            counter++;
                        }
                        if (!values.isEmpty()) {
                            rowsByKey.put(key, values);
                        }
                    }
                });
        var minimized = new LinkedHashMap<String, Object>();
        rowsByKey.keySet()
                .forEach(key -> {
                    var value = rowsByKey.get(key);
                    if(multiValuedKeys.contains(key)) {
                        minimized.put(key, value);
                    }
                    else {
                        if(!value.isEmpty()) {
                            minimized.put(key, value.stream().findFirst().orElse(null));
                        }
                    }
                });
        return minimized;
    }
}
