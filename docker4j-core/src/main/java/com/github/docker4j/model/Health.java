package com.github.docker4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Health {
    @JsonProperty("Status")
    private String status;
    @JsonProperty("FallingStreak")
    private int fallingStreak;
    @JsonProperty("Log")
    private HealthCheckResult[] log;

    public String getStatus() {
        return status;
    }

    public int getFallingStreak() {
        return fallingStreak;
    }

    public HealthCheckResult[] getLog() {
        return log;
    }
}
