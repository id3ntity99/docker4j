package com.github.docker4j.exceptions;

public class DockerResponseException extends RuntimeException{
    public DockerResponseException(String message) {
        super(message);
    }
}
