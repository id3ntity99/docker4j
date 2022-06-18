package client.nettyClient;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestFutureListener implements ChannelFutureListener {
    private final Logger logger = LoggerFactory.getLogger(RequestFutureListener.class);
    private final String uri;

    public RequestFutureListener(String uri) {
        this.uri = uri;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        if (future.isSuccess() && future.isDone()) {
            String loggerInfo = String.format("A request to %s is successfully done", uri);
            logger.info(loggerInfo);
        }
    }
}
