package docker4j.internal.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

@Deprecated
public enum URIs {
    DOCKER_DAEMON("http://localhost:2375"),
    CREATE_CONTAINER("http://localhost:2375/containers/create"),
    START_CONTAINER("http://localhost:2375/containers/%s/start"),
    EXEC_CREATE("http://localhost:2375/containers/%s/exec"),
    EXEC_START("http://localhost:2375/exec/%s/start"),
    STOP_CONTAINER("http://localhost:2375/containers/%s/stop"),
    KILL_CONTAINER("http://localhost:2375/containers/%s/kill");

    private String stringUri;
    private String id;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    URIs(String stringUri) {
        this.stringUri = stringUri;
    }

    public URI uri() {
        URI uri = null;
        try {
            uri = new URI(stringUri);
        } catch(URISyntaxException e) {
           logger.error("Wrong uri", e);
        }
        return uri;
    }

    public URI uri(String id) {
        URI uri = null;
        try {
            uri = new URI(String.format(stringUri, id));
        } catch(URISyntaxException e) {
            logger.error("Wrong uri", e);
        }
        return uri;
    }
}
