package edu.stanford.radx;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-11
 */
public class SheetNameStopList {

    public static final Set<String> sheetNamesStopList;

    static {
        var inputStream = SheetNameStopList.class.getResourceAsStream("/sheet-name-stop-list.txt");
        if (inputStream == null) {
            sheetNamesStopList = Collections.emptySet();
        }
        else {
            var reader = new BufferedReader(new InputStreamReader(inputStream));
            sheetNamesStopList = reader.lines()
                                       .map(String::trim)
                                       .filter(line -> !line.isEmpty())
                                       .filter(line -> !line.startsWith("#"))
                                       .map(line -> line.replace(" ", ""))
                                       .map(line -> line.replace("_", ""))
                                       .map(line -> line.replace("-", ""))
                                       .map(line -> line.replace(".", ""))
                                       .map(line -> line.replace("/", ""))
                                       .map(String::toLowerCase)
                                       .collect(Collectors.toSet());
        }
    }

    public static boolean isStopped(String sheetName) {
        var normalizedSheetName = sheetName.trim()
                                           .toLowerCase()
                                           .replace(" ", "")
                                           .replace("_", "")
                                           .replace("-", "")
                                           .replace(".", "")
                                           .replace("/", "");
        return sheetNamesStopList.contains(normalizedSheetName);
    }
}
