package edu.stanford.radx.cli;

import edu.stanford.radx.Csv;
import edu.stanford.radx.CsvTransformer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-08-09
 */
public class AppendTokensProcessor implements CsvTransformer {

    private final List<String> fieldNames;

    private final List<String> stopWords;

    public AppendTokensProcessor(List<String> fieldNames,
                                 List<String> stopWords) {
        this.fieldNames = new ArrayList<>(fieldNames);
        this.stopWords = new ArrayList<>(stopWords);
    }

    @Override
    public List<Csv> transformCsv(Csv csv) {
        var extendedContent = csv.content()
                .stream()
                .map(row -> {
                    var joinedTokenFields = fieldNames.stream()
                              .map(csv::getIndex)
                              .filter(index -> index != -1 && index < row.size())
                              .map(row::get)
                            .collect(Collectors.joining(" "));
                    System.out.print(joinedTokenFields + "----------------");
                    try {
                        var tokens = analyze(joinedTokenFields, new StandardAnalyzer());
                        var joinedTokens = tokens.stream().collect(Collectors.joining("|"));
                        var extendedRow = new ArrayList<>(row);
                        extendedRow.add(joinedTokens);
                        System.out.println(joinedTokens);
                        return extendedRow;
                    } catch (IOException e) {
                        return row;
                    }
                })
                .toList();
        var extendedHeader = new ArrayList<String>(csv.header());
        extendedHeader.add("tokens");
        return List.of(new Csv(csv.coordinates(), extendedHeader, extendedContent));
    }


    public List<String> analyze(String text, Analyzer analyzer) throws IOException {
        var result = new ArrayList<String>();
        var tokenStream = analyzer.tokenStream("main", text);
        var filter = new StopFilter(tokenStream, StopFilter.makeStopSet(stopWords));
        CharTermAttribute attr = filter.addAttribute(CharTermAttribute.class);
        filter.reset();
        while(filter.incrementToken()) {
            result.add(attr.toString());
        }
        return result;
    }
}
