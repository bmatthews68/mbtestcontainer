package com.btmatthews.testcontainers.mountebank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MountebankConfiguration {

    @JsonProperty("imposters")
    private final List<ImposterDefinition> imposters = new ArrayList<>();

    @JsonCreator
    public MountebankConfiguration(@JsonProperty("imposters") final List<ImposterDefinition> imposters) {
        this.imposters.addAll(imposters);
    }

    public List<ImposterDefinition> getImposters() {
        return imposters;
    }
}
