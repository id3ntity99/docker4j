package com.github.docker4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MountPoint {
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Source")
    private String source;
    @JsonProperty("Destination")
    private String destination;
    @JsonProperty("Driver")
    private String driver;
    @JsonProperty("Mode")
    private String mode;
    @JsonProperty("RW")
    private boolean rw;
    @JsonProperty("Propagation")
    private String propagation;

    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getDriver() {
        return driver;
    }

    public String getMode() {
        return mode;
    }

    public boolean isRw() {
        return rw;
    }

    public String getPropagation() {
        return propagation;
    }
}
