package com.github.docker4j;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class CreateContainerResponse implements DockerResponse{
    @JsonProperty("Id")
    private String containerId;
    @JsonProperty("Warnings")
    private String[] warnings;

    @Override
    public String getContainerId() {
        return containerId;
    }

    public String[] getWarnings() {
        return warnings;
    }

    @Override
    @JsonIgnore
    public String[] getExecIds() {
        return new String[0];
    }
}
