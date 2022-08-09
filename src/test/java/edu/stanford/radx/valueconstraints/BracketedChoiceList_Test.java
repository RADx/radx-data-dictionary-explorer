package edu.stanford.radx.valueconstraints;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BracketedChoiceList_Test {

    @Test
    void shouldCheckSingleElementChoiceList() {
        var choiceList = BracketedChoiceList.isChoiceList("[\"ab\"]");
        assertThat(choiceList).isTrue();
    }

    @Test
    void shouldCheckSingleElementChoiceListWithWhiteSpace() {
        var choiceList = BracketedChoiceList.isChoiceList(" [ \"ab\" ] ");
        assertThat(choiceList).isTrue();
    }

    @Test
    void shouldCheckMultiElementChoiceList() {
        var choiceList = BracketedChoiceList.isChoiceList("[\"ab\",\"cd\"]");
        assertThat(choiceList).isTrue();
    }

    @Test
    void shouldCheckMultiElementChoiceListWithWhiteSpace() {
        var choiceList = BracketedChoiceList.isChoiceList("[\"ab\" , \"cd\"]");
        assertThat(choiceList).isTrue();
    }

    @Test
    void shouldCheckChoiceEmptyList() {
        var choiceList = BracketedChoiceList.isChoiceList("[]");
        assertThat(choiceList).isTrue();
    }

    @Test
    void shouldCheckEmptyChoiceListWithWhiteSpace() {
        var choiceList = BracketedChoiceList.isChoiceList("[ ]");
        assertThat(choiceList).isTrue();
    }



    @Test
    void shouldParseSingleElementChoiceList() {
        var choiceList = BracketedChoiceList.parseChoiceList("[\"ab\"]");
        assertThat(choiceList).contains("ab");
    }

    @Test
    void shouldParseSingleElementChoiceListWithWhiteSpace() {
        var choiceList = BracketedChoiceList.parseChoiceList(" [ \"ab\" ] ");
        assertThat(choiceList).contains("ab");
    }

    @Test
    void shouldParseMultiElementChoiceList() {
        var choiceList = BracketedChoiceList.parseChoiceList("[\"ab\",\"cd\"]");
        assertThat(choiceList).contains("ab","cd");
    }

    @Test
    void shouldParseMultiElementChoiceListWithSingleQuotes() {
        var choiceList = BracketedChoiceList.parseChoiceList("['ab',\"cd\"]");
        assertThat(choiceList).contains("ab","cd");
    }

    @Test
    void shouldParseMultiElementChoiceListWithDoubleDoubleQuotes() {
        var choiceList = BracketedChoiceList.parseChoiceList("[\"\"ab\"\",\"cd\"]");
        assertThat(choiceList).contains("ab","cd");
    }

    @Test
    void shouldParseMultiElementChoiceListWithWhiteSpace() {
        var choiceList = BracketedChoiceList.parseChoiceList("[\"ab\" , \"cd\"]");
        assertThat(choiceList).contains("ab","cd");
    }

    @Test
    void shouldParseChoiceEmptyList() {
        var choiceList = BracketedChoiceList.parseChoiceList("[]");
        assertThat(choiceList).isEmpty();
    }

    @Test
    void shouldParseEmptyChoiceListWithWhiteSpace() {
        var choiceList = BracketedChoiceList.parseChoiceList("[ ]");
        assertThat(choiceList).isEmpty();
    }
}