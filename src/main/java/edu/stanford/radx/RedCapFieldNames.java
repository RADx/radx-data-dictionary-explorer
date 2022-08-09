package edu.stanford.radx;

import java.util.HashSet;
import java.util.Set;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2022-07-05
 */
public enum RedCapFieldNames {

    VARIABLE_NAME("Variable / Field Name"),
    FORM_NAME("Form Name"),
    SECTION_HEADER("Section Header"),
    FIELD_TYPE("Field Type"),
    FIELD_LABEL("Field Label"),
    CHOICES_CALCULATIONS_OR_SLIDER_LABELS("Choices, Calculations, OR Slider Labels"),
    FIELD_NOTES("Field Note"),
    TEXT_VALIDATION_TYPE_OR_SHOW_SLIDER_NUMBER("Text Validation Type OR Show Slider Number"),
    TEXT_VALIDATION_MIN("Text Validation Min"),
    TEXT_VALIDATION_MAX("Text Validation Max"),
    IDENTIFIER("Identifier?"),
    BRANCHING_LOGIC("Branching Logic (Show field only if...)"),
    REQUIRED_FIELD("Required Field?"),
    CUSTOM_ALIGNMENT("Custom Alignment"),
    QUESTION_NUMBER("Question Number (surveys only)"),
    MATRIX_GROUP_NAME("Matrix Group Name"),
    MATRIX_RANKING("Matrix Ranking?"),
    FIELD_ANNOTATION("Field Annotation");

    private final static Set<String> printNames = new HashSet<>();

    static {
        for(var value : values()) {
            printNames.add(value.getPrintName().toLowerCase());
        }
    }

    private final String printName;

    RedCapFieldNames(String printName) {
        this.printName = printName;
    }

    public String getPrintName() {
        return printName;
    }

    public static boolean isRedCapFieldName(String printName) {
        return printNames.contains(printName.toLowerCase());
    }
}
