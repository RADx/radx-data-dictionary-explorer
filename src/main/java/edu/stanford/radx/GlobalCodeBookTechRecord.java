package edu.stanford.radx;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-07-10
 */
public record GlobalCodeBookTechRecord(@JsonProperty("Concept") String concept,
                                      @JsonProperty("RADx Global Prompt") String globalPrompt,
                                      @JsonProperty("RADx Global Variable") String globalVariable,
                                      @JsonProperty("RADx Global Response Options") String globalResponse,
                                      @JsonProperty("RADx-TECH Prompt") String techPrompt,
                                      @JsonProperty("RADx-TECH Variable") String techVariable,
                                      @JsonProperty("RADx-TECH Response Options") String techResponse,
                                      @JsonProperty("Comment") String comment)  {
}
