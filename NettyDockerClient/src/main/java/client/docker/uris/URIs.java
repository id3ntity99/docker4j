package client.docker.uris;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum URIs {
    DOCKER_DAEMON("http://localhost:2375"),
    CREATE_CONTAINER("http://localhost:2375/containers/create"),
    START_CONTAINER("http://localhost:2375/containers/%s/start");

    private String uri;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    URIs(String stringUri) {
        this.uri = stringUri;
    }

    public String uri() {
        return uri;
    }
}
