package client.docker.request;

import client.docker.dockerclient.exceptions.DockerResponseException;
import client.docker.model.Container;
import client.docker.uris.URIs;
import client.docker.util.RequestHelper;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;

import java.net.URI;

public class StartContainerRequest extends DockerRequest {
    public StartContainerRequest(Builder builder) {
        super(builder);
    }

    @Override
    protected StartContainerRequest setContainer(Container container) {
        this.container = container;
        return this;
    }

    @Override
    protected DockerRequest setNextRequest(DockerRequest nextRequest) {
        this.nextRequest = nextRequest;
        return this;
    }

    @Override
    protected DockerRequest setAllocator(ByteBufAllocator allocator) {
        return null;
    }

    @Override
    public DockerResponseHandler handler() {
        return new StartContainerResponseHandler(container, nextRequest);
    }

    @Override
    public FullHttpRequest render() {
        String containerId = container.getContainerId();
        URI uri = URIs.START_CONTAINER.uri(containerId);
        logger.debug(String.format("Rendered FullHttpRequest. URL == %s", uri));
        return RequestHelper.post(uri, false, null, null);
    }

    public static class Builder implements DockerRequest.Builder {
        @Override
        public DockerRequest build() {
            return new StartContainerRequest(this);
        }
    }

    private static class StartContainerResponseHandler extends DockerResponseHandler {
        public StartContainerResponseHandler(Container container, DockerRequest nextRequest) {
            super(container, nextRequest);
            this.container = container;
            this.nextRequest = nextRequest;
        }

        private void handleResponse(ChannelHandlerContext ctx, FullHttpResponse res) throws Exception {
            if (nextRequest != null) {
                logger.debug(String.format("Next request detected: %s", nextRequest.getClass().getSimpleName()));
                nextRequest.setContainer(container);
                FullHttpRequest req = nextRequest.render();
                ctx.channel().writeAndFlush(req).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            logger.debug(String.format("Successfully sent %s", nextRequest.getClass().getSimpleName()));
                            ctx.pipeline().replace(StartContainerResponseHandler.class, nextRequest.toString(), nextRequest.handler());
                        } else {
                            String errMsg = String.format("Exception raised while sending next %s", nextRequest.getClass().getSimpleName());
                            logger.error(errMsg, future.cause());
                        }
                    }
                });
            } else {
                logger.info(String.format("There are no more requests... removing %s", this.getClass().getSimpleName()));
                ctx.pipeline().remove(this);
            }
        }

        @Override
        public void channelRead0(ChannelHandlerContext ctx, FullHttpResponse res) throws Exception {
            if (res.status().code() == 204) {
                handleResponse(ctx, res);
            } else {
                String errMessage = String.format("Unsuccessful response detected: %s %s",
                        res.status().toString(),
                        res.content().toString(CharsetUtil.UTF_8));
                throw new DockerResponseException(errMessage);
            }
        }
    }
}
