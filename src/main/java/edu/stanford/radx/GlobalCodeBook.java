package edu.stanford.radx;

import java.util.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-07-10
 */
public class GlobalCodeBook {

    private final List<GlobalCodeBookRecord> records;

    private final Map<String, GlobalCodeBookRecord> map;

    private final Map<String, GlobalCodeBookUpRecord> upMap;

    private final Map<String, GlobalCodeBookTechRecord> techMap;

    private final Map<String, GlobalCodeBookRadRecord> radMap;

    private final Map<String, GlobalCodeBookDhtRecord> dhtMap;

    private final Set<String> allVariableNames;

    public static GlobalCodeBook get(List<GlobalCodeBookRecord> records,
                                     List<GlobalCodeBookUpRecord> upRecords,
                                     List<GlobalCodeBookTechRecord> techRecords,
                                     List<GlobalCodeBookRadRecord> radRecords,
                                     List<GlobalCodeBookDhtRecord> dhtRecords) {

        var map = new HashMap<String, GlobalCodeBookRecord>();
        records.forEach(r -> map.put(r.variable().toLowerCase(), r));

        var upMap = new HashMap<String, GlobalCodeBookUpRecord>();
        upRecords.forEach(r -> upMap.put(r.upVariable().toLowerCase(), r));

        var techMap = new HashMap<String, GlobalCodeBookTechRecord>();
        techRecords.forEach(r -> techMap.put(r.techVariable().toLowerCase(), r));

        var radMap = new HashMap<String, GlobalCodeBookRadRecord>();
        radRecords.forEach(r -> radMap.put(r.radVariable().toLowerCase(), r));

        var dhtMap = new HashMap<String, GlobalCodeBookDhtRecord>();
        dhtRecords.forEach(r -> dhtMap.put(r.dhtVariable().toLowerCase(), r));

        var allVariableNames = new HashSet<String>(map.keySet());
        addAllVariables(upMap.keySet(), allVariableNames);
        addAllVariables(dhtMap.keySet(), allVariableNames);
        addAllVariables(techMap.keySet(), allVariableNames);
        addAllVariables(radMap.keySet(), allVariableNames);


        return new GlobalCodeBook(records, map, upMap, techMap, radMap, dhtMap, allVariableNames);
    }

    private static void addAllVariables(Set<String> vars, Set<String> allVariableNames) {
        vars.stream()
                .map(v -> v.split(";"))
                .flatMap(Arrays::stream)
                .forEach(allVariableNames::add);
    }

    private GlobalCodeBook(List<GlobalCodeBookRecord> records,
                           Map<String, GlobalCodeBookRecord> map,
                           Map<String, GlobalCodeBookUpRecord> upMap,
                           Map<String, GlobalCodeBookTechRecord> techMap,
                           Map<String, GlobalCodeBookRadRecord> radMap,
                           Map<String, GlobalCodeBookDhtRecord> dhtMap,
                           Set<String> allVariableNames) {
        this.records = records;
        this.map = map;
        this.upMap = upMap;
        this.techMap = techMap;
        this.radMap = radMap;
        this.dhtMap = dhtMap;
        this.allVariableNames = allVariableNames;
    }

    public Optional<GlobalCodeBookRecord> getRecord(String variable) {
        return Optional.ofNullable(map.get(variable));
    }

    public boolean isGlobalVariable(String name) {
        return map.containsKey(name);
    }

    public boolean isVariable(String name) {
        return allVariableNames.contains(name.toLowerCase());
    }

    public List<GlobalCodeBookRecord> getRecords() {
        return new ArrayList<>(records);
    }
}
