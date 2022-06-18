package client.docker.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

public enum URIs {
    DOCKER_DAEMON("http://localhost:2375"),
    CREATE_CONTAINER("http://localhost:2375/containers/create");

    private URI uri;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    URIs(String uriString) {
        try {
            this.uri = new URI(uriString);
        } catch(URISyntaxException e) {
           logger.error("Something's wrong about uri...", e);
        }
    }

    public URI uri() {
        return uri;
    }
}
