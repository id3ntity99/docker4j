package client.docker.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract command that the Docker-client commands must extend.
 * All commands follow the Command design pattern (but without the Sender object).
 *
 * @param <T> A response returned by {@link AbstractCommand#exec()}.
 * @see <a href="https://refactoring.guru/design-patterns/command">Command pattern</a>
 */
public abstract class AbstractCommand<T> {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Create {@link io.netty.handler.codec.http.FullHttpRequest} and send it via {@link client.nettyclient.HttpClient}.
     * This method returns corresponding command response such as {@link CreateContainerResponse}.
     * @return A response corresponds to the command. Users should be able to map the response to the JSON string.
     * @throws Exception Throws an exception if the errorneous response has been received such as 400 BAD REQUEST.
     */
    public abstract T exec() throws Exception;
}
