package edu.stanford.radx;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-07-10
 */
public record GlobalCodeBookRecord(@JsonProperty("Concept") String concept,
                                   @JsonProperty("RADx Global Prompt") String prompt,
                                   @JsonProperty("Variable") String variable,
                                   @JsonProperty("Responses") String responses) {
}
