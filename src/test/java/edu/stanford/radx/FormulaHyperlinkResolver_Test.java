package edu.stanford.radx;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FormulaHyperlinkResolver_Test {

    @Test
    void shouldFindFieldName() {
        var example = "=HYPERLINK(\"#\"&CELL(\"address\",INDEX('Read Me'!A1,MATCH(\"Blah\",'Read Me'!A1))),\"Friendly Name\")";
        var resolver = new FormulaHyperlinkResolver();
        var resolvedName = resolver.getResolvedFieldName(example);
        assertThat(resolvedName).isEqualTo("Friendly Name");
    }
}