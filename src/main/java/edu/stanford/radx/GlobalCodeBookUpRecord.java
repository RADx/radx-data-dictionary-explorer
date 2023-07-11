package edu.stanford.radx;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-07-10
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GlobalCodeBookUpRecord(@JsonProperty("Concept") String concept,
                                     @JsonProperty("RADx Global Prompt") String globalPrompt,
                                     @JsonProperty("RADx Global Variable") String globalVariable,
                                     @JsonProperty("RADx Global Response Options") String globalResponse,
                                     @JsonProperty("RADx-UP Prompt") String upPrompt,
                                     @JsonProperty("RADx-UP Variable") String upVariable,
                                     // Note the misspelling of response
                                     @JsonProperty("RADx-UP Reponse Options") String upResponse,
                                     @JsonProperty("Comment") String comment) {
}
