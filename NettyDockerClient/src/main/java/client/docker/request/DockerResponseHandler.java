package client.docker.request;

import client.docker.model.Container;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DockerResponseHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    protected static final ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    protected DockerRequest nextRequest = null;
    protected Container container;
    protected Promise<Container> promise;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    DockerResponseHandler(Container container, DockerRequest nextRequest, Promise<Container> promise) {
        this.container = container;
        this.nextRequest = nextRequest;
        this.promise = promise;
    }

    public static class NextRequestListener implements ChannelFutureListener {
        private final Logger logger;
        private final ChannelHandlerContext ctx;
        private final DockerRequest nextRequest;

        public NextRequestListener(Logger logger, ChannelHandlerContext ctx, DockerRequest nextRequest) {
            this.logger = logger;
            this.ctx = ctx;
            this.nextRequest = nextRequest;
        }

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
                logger.debug(String.format("Successfully sent %s", nextRequest.getClass().getSimpleName()));
                DockerResponseHandler nextHandler = nextRequest.handler();
                ctx.pipeline().replace(ExecCreateHandler.class, nextHandler.toString(), nextHandler);
            } else {
                String errMsg = String.format("Exception raised while sending next %s", nextRequest.getClass().getSimpleName());
                logger.error(errMsg, future.cause());
            }

        }
    }
}
