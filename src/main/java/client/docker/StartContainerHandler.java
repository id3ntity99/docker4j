package client.docker;

import client.docker.exceptions.DockerResponseException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;

final class StartContainerHandler extends DockerResponseHandler {
    private void parseResponseBody(String json) {
        node.add("start_container_response", json);
    }

    private void handleResponse(ChannelHandlerContext ctx) throws Exception {
        if (nextRequest != null) {
            logger.debug("Next request detected: {}", nextRequest.getClass().getSimpleName());
            handOver();
            FullHttpRequest req = nextRequest.render();
            ctx.channel().writeAndFlush(req).addListener(new NextRequestListener(logger, ctx, this, nextRequest));
        } else {
            logger.info("There are no more requests... removing {}", this.getClass().getSimpleName());
            promise.setSuccess(node);
            ctx.pipeline().remove(this);
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpResponse res) throws Exception {
        if (res.status().code() == 204) {
            parseResponseBody(res.content().toString(CharsetUtil.UTF_8));
            handleResponse(ctx);
        } else {
            String errMessage = String.format("Unsuccessful response detected: %s %s",
                    res.status().toString(),
                    res.content().toString(CharsetUtil.UTF_8));
            node.add("_error", errMessage);
            promise.setSuccess(node);
            throw new DockerResponseException(errMessage);
        }
    }
}
