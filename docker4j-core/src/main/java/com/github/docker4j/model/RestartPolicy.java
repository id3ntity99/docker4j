package com.github.docker4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RestartPolicy {
    @JsonProperty("Name")
    private String name;
    @JsonProperty("MaximumRetryCount")
    private int maxRetryCount;

    public RestartPolicy(String name, int maxRetryCount) {
        this.name = name;
        this.maxRetryCount = maxRetryCount;
    }

    public String getName() {
        return name;
    }

    public int getMaxRetryCount() {
        return maxRetryCount;
    }
}
