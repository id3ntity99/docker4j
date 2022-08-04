package com.github.docker4j.internal.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public class EndpointUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(EndpointUtil.class);
    private static final String CREATE_CONTAINER = "http://localhost:2374/containers/create";
    private static final String START_CONTAINER = "http://localhost:2375/containers/%s/start";
    private static final String EXEC_CREATE = "http://localhost:2375/containers/%s/exec";
    private static final String EXEC_START = "http://localhost:2375/exec/%s/start";
    private static final String STOP_CONTAINER = "http://localhost:2375/containers/%s/stop?t=%d";
    private static final String KILL_CONTAINER = "http://localhost:2375/containers/%s/kill";
    private static final String REMOVE_CONTAINER = "http://localhost:2375/containers/%s?v=%b&froce=%b&link=%b";
    private static final String INSPECT_CONTAINER = "http://localhost:2375/containers/%s/json?size=%b";

    private EndpointUtil() {

    }

    private static URI generateURI(String stringUri) {
        URI uri = null;
        try {
            uri = new URI(stringUri);
        } catch (URISyntaxException e) {
            LOGGER.error("Wrong endpoint", e.getCause());
        }
        return uri;
    }

    public static URI createContainer() {
        return generateURI(CREATE_CONTAINER);
    }

    public static URI startContainer(String containerId) {
        String stringUri = String.format(START_CONTAINER, containerId);
        return generateURI(stringUri);
    }

    public static URI stopContainer(String containerId, int t) {
        String stringUri = String.format(STOP_CONTAINER,
                containerId,
                t);
        return generateURI(stringUri);
    }

    public static URI killContainer(String containerId) {
        String stringUri = String.format(KILL_CONTAINER, containerId);
        return generateURI(stringUri);
    }

    public static URI removeContainer(String containerId, boolean v, boolean force, boolean link) {
        String stringUri = String.format(REMOVE_CONTAINER, containerId, v, force, link);
        return generateURI(stringUri);
    }

    public static URI execCreate(String containerId) {
        String stringUri = String.format(EXEC_CREATE, containerId);
        return generateURI(stringUri);
    }

    public static URI execStart(String execId) {
        String stringUri = String.format(EXEC_START, execId);
        return generateURI(stringUri);
    }

    public static URI inspectContainer(String containerId, boolean size) {
        String stringUri = String.format(INSPECT_CONTAINER, containerId, size);
        return generateURI(stringUri);
    }
}
