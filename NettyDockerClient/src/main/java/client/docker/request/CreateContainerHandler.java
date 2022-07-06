package client.docker.request;

import client.docker.dockerclient.exceptions.DockerResponseException;
import client.docker.model.Container;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Promise;

class CreateContainerHandler extends DockerResponseHandler {
    public CreateContainerHandler(Container container, DockerRequest nextRequest,
                                  Promise<Container> promise, ByteBufAllocator allocator) {
        super(container, nextRequest, promise, allocator);
    }

    private void handleResponse(ChannelHandlerContext ctx, FullHttpResponse res) throws Exception {
        String jsonBody = res.content().toString(CharsetUtil.UTF_8);
        String containerId = mapper.readTree(jsonBody).get("Id").asText();
        container.setContainerId(containerId);
        if (nextRequest != null) {
            logger.debug("Next request detected: {}", nextRequest.getClass().getSimpleName());
            handOver();
            FullHttpRequest nextHttpReq = nextRequest.render();
            ctx.channel().writeAndFlush(nextHttpReq).addListener(new NextRequestListener(logger, ctx, this, nextRequest));
        } else {
            logger.info("There are no more requests... removing {}", this.getClass().getSimpleName());
            promise.setSuccess(container);
            ctx.pipeline().remove(this);
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpResponse res) throws Exception {
        if (res.status().code() == 201)
            handleResponse(ctx, res);
        else {
            String errMessage = String.format("Unsuccessful response detected: %s %s",
                    res.status().toString(),
                    res.content().toString(CharsetUtil.UTF_8));
            throw new DockerResponseException(errMessage);
        }
    }
}
