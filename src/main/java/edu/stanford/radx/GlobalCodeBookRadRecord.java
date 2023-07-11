package edu.stanford.radx;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-07-10
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GlobalCodeBookRadRecord(@JsonProperty("Concept") String concept,
                                      @JsonProperty("RADx Global Prompt") String globalPrompt,
                                      @JsonProperty("RADx Global Variable") String globalVariable,
                                      @JsonProperty("RADx Global Response Options") String globalResponse,
                                      @JsonProperty("RADx-rad Prompt") String radPrompt,
                                      @JsonProperty("RADx-rad Variable") String radVariable,
                                      @JsonProperty("RADx-rad Response Options") String radResponse,
                                      @JsonProperty("Comment") String comment) {

}
