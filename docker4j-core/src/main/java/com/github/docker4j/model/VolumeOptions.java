package com.github.docker4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class VolumeOptions {
    @JsonProperty("NoCopy")
    private boolean noCopy = false;
    @JsonProperty("Labels")
    private Map<String, String> labels;
    @JsonProperty("DriverConfig")
    private DriverConfig driverConfig;

    public VolumeOptions setDriverConfig(DriverConfig driverConfig) {
        this.driverConfig = driverConfig;
        return this;
    }

    public VolumeOptions setLabels(Map<String, String> labels) {
        this.labels = labels;
        return this;
    }

    public VolumeOptions setNoCopy(boolean noCopy) {
        this.noCopy = noCopy;
        return this;
    }

    public DriverConfig getDriverConfig() {
        return driverConfig;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public boolean getNoCopy() {
        return noCopy;
    }

    public static class DriverConfig {
        @JsonProperty("Name")
        private String name;
        @JsonProperty("Options")
        private Map<String, String> options;

        public DriverConfig setName(String name) {
            this.name = name;
            return this;
        }

        public DriverConfig setOptions(Map<String, String> options) {
            this.options = options;
            return this;
        }

        public String getName() {
            return name;
        }

        public Map<String, String> getOptions() {
            return options;
        }
    }
}
