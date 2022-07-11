package client.docker;

import client.docker.exceptions.DockerResponseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;

final class CreateContainerHandler extends DockerResponseHandler {

    private void parseResponseBody(String json) throws JsonProcessingException {
        JsonNode jsonNode = JacksonHelper.toNode(json);
        String containerId = jsonNode.get("Id").asText();
        node.add("create_container_response", jsonNode);
        node.add("_container_id", containerId);
    }
    private void handleResponse(ChannelHandlerContext ctx, FullHttpResponse res) throws Exception {
        String json = res.content().toString(CharsetUtil.UTF_8);
        parseResponseBody(json);
        if (nextRequest != null) {
            logger.debug("Next request detected: {}", nextRequest.getClass().getSimpleName());
            handOver();
            FullHttpRequest nextHttpReq = nextRequest.render();
            ctx.channel().writeAndFlush(nextHttpReq).addListener(new NextRequestListener(logger, ctx, this, nextRequest));
        } else {
            logger.info("There are no more requests... removing {}", this.getClass().getSimpleName());
            promise.setSuccess(node);
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
            node.add("_error", errMessage);
            promise.setSuccess(node);
            throw new DockerResponseException(errMessage);
        }
    }
}
