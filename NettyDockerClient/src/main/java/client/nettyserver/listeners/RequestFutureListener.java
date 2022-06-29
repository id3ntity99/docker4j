package client.nettyserver.listeners;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/**
 * An implementation of the {@link ChannelFutureListener}.
 * This class instance is used for checking and logging that the
 * request (using {@link io.netty.channel.Channel#writeAndFlush(Object)}) was successful.
 * If the request was unsuccessful, this logs the cause.
 */
public class RequestFutureListener implements ChannelFutureListener {
    private final Logger logger = LoggerFactory.getLogger(RequestFutureListener.class);
    private final String uri;

    /**
     * A constructor that receives {@link URI} parameter and convert to string for logging.
     * @param uri A {@link URI} for logging.
     */
    public RequestFutureListener(URI uri) {
        this.uri = uri.toString();
    }


    /**
     * Invoked when the operation was completed.
     * @param future  the source {@link ChannelFuture} which called this callback
     */
    @Override
    public void operationComplete(ChannelFuture future) {
        if (future.isSuccess() && future.isDone()) {
            String loggerInfo = String.format("A request to %s is successfully done", uri);
            logger.info(loggerInfo);
        } else {
            logger.debug("Exception raised while writing...", future.cause());
        }
    }
}
