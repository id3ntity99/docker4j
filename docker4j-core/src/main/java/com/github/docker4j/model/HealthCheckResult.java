package com.github.docker4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HealthCheckResult {
    @JsonProperty("Start")
    private String start;
    @JsonProperty("Emd")
    private String emd;
    @JsonProperty("ExitCode")
    private int exitCode;
    @JsonProperty("Output")
    private String output;

    public String getStart() {
        return start;
    }

    public String getEmd() {
        return emd;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getOutput() {
        return output;
    }
}
