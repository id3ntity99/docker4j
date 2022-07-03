package client.docker.uris;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum URIs {
    DOCKER_DAEMON("http://localhost:2375"),
    CREATE_CONTAINER("http://localhost:2375/containers/create"),
    START_CONTAINER("http://localhost:2375/containers/%s/start"),
    EXEC_CREATE("http://localhost:2375/containers/%s/exec"),
    EXEC_START("http://localhost:2375/exec/%s/start"),
    KILL_CONTAINER("http://localhost:2375/containers/%s/kill");

    private String stringUri;
    private String id;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    URIs(String stringUri) {
        this.stringUri = stringUri;
    }

    public String uri() {
        return stringUri;
    }

    public String uri(String id) {
        return String.format(stringUri, id);
    }
}
