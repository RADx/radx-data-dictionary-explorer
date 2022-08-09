package edu.stanford.radx.cli;

import edu.stanford.radx.Csv;
import edu.stanford.radx.PlainCsvFileCoordinates;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static picocli.CommandLine.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-25
 */
@Component
@Command(name = "split", description = "Splits a CSV file into multiple files based on the values of a column")
public class SplitCommand implements CliCommand {

    private final CliCsvTransformerProcessor processor;

    @Mixin
    IoMixin io;

    @Option(names = "--field-names", required = true, split = ",", description = "A list of field names on which to split.  The CSV will be split based on the value of the tuple formed my the field values")
    public List<String> fieldNames;

    public SplitCommand(CliCsvTransformerProcessor processor) {
        this.processor = processor;
    }

    @Override
    public Integer call() throws Exception {
        processor.transformCsvFiles(io.in, io.out, csv -> {
            var splitIndexes = fieldNames.stream()
                                                  .map(csv::getIndex)
                                                  .toList();
            if(splitIndexes.contains(-1)) {
                return List.of(csv);
            }
            else {
                return csv.content().stream()
                          .map(row -> {
                       var key = new ArrayList<String>();
                       for(var i : splitIndexes) {
                           if(i < row.size()) {
                               key.add(row.get(i));
                           }
                       }
                       return new KeyedRow(key, row);
                   })
                          .collect(Collectors.groupingBy(kr -> kr.key, Collectors.toList()))
                          .entrySet()
                          .stream()
                          .map((e) -> {
                       var k = e.getKey();
                       var rows = e.getValue();
                       var join = String.join(" - ", k);
                       var fileName = csv.coordinates().getDescription() + " - " + join + ".csv";
                       fileName = fileName.replace('/', ' ');
                       var csvPath = csv.coordinates().getParentDirectory()
                                        .resolve(fileName);
                       var csvCoors =  new PlainCsvFileCoordinates(csvPath);
                       return new Csv(csvCoors, csv.header(), rows.stream().map(r -> r.row).collect(
                               Collectors.toList()));
                   }).toList();
            }
        });
        return 0;
    }

    private record KeyedRow(List<String> key, List<String> row)  {

    }
}
