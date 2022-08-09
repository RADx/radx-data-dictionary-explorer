package edu.stanford.radx.valueconstraints;

import edu.stanford.radx.BracketedChoiceListBaseListener;
import edu.stanford.radx.BracketedChoiceListParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-24
 */
public class BracketedChoiceListParseListener extends BracketedChoiceListBaseListener {

    private final List<String> choices = new ArrayList<>();

    @Override
    public void enterChoices(BracketedChoiceListParser.ChoicesContext ctx) {
        choices.clear();
    }

    @Override
    public void exitChoice(BracketedChoiceListParser.ChoiceContext ctx) {
        var choice = ctx.getText().trim();
        if(choice.startsWith("\"\"") && choice.endsWith("\"\"") && choice.length() >= 4) {
            choices.add(choice.substring(2, choice.length() - 2));
        }
        else if(choice.startsWith("\"") && choice.endsWith("\"") && choice.length() >= 2) {
            choices.add(choice.substring(1, choice.length() - 1));
        }
        else if(choice.startsWith("'") && choice.endsWith("'") && choice.length() >= 2) {
            choices.add(choice.substring(1, choice.length() - 1));
        }
        else if(!choice.isEmpty()) {
            choices.add(choice);
        }
    }

    public List<String> getParsedChoices() {
        return new ArrayList<>(choices);
    }
    }
