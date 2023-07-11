package edu.stanford.radx;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2023-07-10
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GlobalCodeBookDhtRecord(@JsonProperty("Concept") String concept,
                                     @JsonProperty("RADx Global Prompt") String globalPrompt,
                                     @JsonProperty("RADx Global Variable") String globalVariable,
                                     @JsonProperty("RADx Global Response Options") String globalResponse,
                                     @JsonProperty("DHT Prompt") String dhtPrompt,
                                     @JsonProperty("DHT Variable") String dhtVariable,
                                     @JsonProperty("DHT Response Options") String dhtResponse,
                                     @JsonProperty("Comment") String comment) {

}
