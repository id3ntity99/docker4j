package com.github.docker4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class GraphDriver {
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Data")
    private Map<String, String> graphDriverData;

    public String getName() {
        return name;
    }

    public Map<String, String> getGraphDriverData() {
        return graphDriverData;
    }
}
