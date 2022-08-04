package com.github.docker4j;

public interface DockerResponse {
    String getContainerId();
    String[] getExecIds();
}
