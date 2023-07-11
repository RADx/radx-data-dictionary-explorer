package edu.stanford.radx;

import java.util.regex.Pattern;

public record FilterSpecification(String fieldName, Pattern pattern) {

}
