package edu.stanford.radx.valueconstraints;

import edu.stanford.radx.BracketedChoiceListLexer;
import edu.stanford.radx.BracketedChoiceListParser;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.RecognitionException;

import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-24
 */
public class BracketedChoiceList {

    public static boolean isChoiceList(String s) {
        try {
            var ts = new BufferedTokenStream(new BracketedChoiceListLexer(CharStreams.fromString(s)));
            var p = new BracketedChoiceListParser(ts);
            p.choices();
            return true;
        } catch (RecognitionException e) {
            return false;
        }
    }

    public static List<String> parseChoiceList(String s) {
        var ts = new BufferedTokenStream(new BracketedChoiceListLexer(CharStreams.fromString(s)));
        var p = new BracketedChoiceListParser(ts);
        var listener = new BracketedChoiceListParseListener();
        p.addParseListener(listener);
        p.choices();
        return listener.getParsedChoices();
    }
}
