package com.github.docker4j;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExecCreateResponse implements DockerResponse {
    @JsonProperty("Id")
    private String execId;

    @Override
    public String[] getExecIds() {
        return new String[]{execId};
    }

    @Override
    public String getContainerId() {
        /*This method returns null since exec create doesn't contain container id*/
        return null;
    }
}
