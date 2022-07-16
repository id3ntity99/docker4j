package client.docker;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DockerResponseHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    protected static final ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    protected DockerRequest nextRequest;
    protected DockerResponseNode node;
    protected Promise<DockerResponseNode> promise;
    protected ByteBufAllocator allocator;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected DockerResponseHandler setPromise(Promise<DockerResponseNode> promise) {
        this.promise = promise;
        return this;
    }

    protected DockerResponseHandler setNextRequest(DockerRequest nextRequest) {
        this.nextRequest = nextRequest;
        return this;
    }

    protected DockerResponseHandler setAllocator(ByteBufAllocator allocator) {
        this.allocator = allocator;
        return this;
    }

    protected DockerResponseHandler setNode(DockerResponseNode node) {
        this.node = node;
        return this;
    }

    protected void handOver() {
        nextRequest.setPromise(promise)
                .setAllocator(allocator)
                .setNode(node);
    }

    protected static class NextRequestListener implements ChannelFutureListener {
        private final Logger logger;
        private final ChannelHandlerContext ctx;
        private final DockerResponseHandler currentHandler;
        private final DockerRequest nextRequest;

        public NextRequestListener(Logger logger, ChannelHandlerContext ctx, DockerResponseHandler currentHandler, DockerRequest nextRequest) {
            this.logger = logger;
            this.ctx = ctx;
            this.currentHandler = currentHandler;
            this.nextRequest = nextRequest;
        }

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
                logger.debug("Successfully sent {}", nextRequest.getClass().getSimpleName());
                ChannelInboundHandlerAdapter nextHandler = nextRequest.handler();
                ctx.pipeline().replace(currentHandler.getClass(), nextHandler.toString(), nextHandler);
            } else {
                String errMsg = String.format("Exception raised while sending next %s", nextRequest.getClass().getSimpleName());
                logger.error(errMsg, future.cause());
            }
        }
    }
}
