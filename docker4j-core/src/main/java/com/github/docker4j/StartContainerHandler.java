package com.github.docker4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.docker4j.exceptions.DockerResponseException;
import com.github.docker4j.internal.http.EndpointUtil;
import com.github.docker4j.internal.http.RequestHelper;
import com.github.docker4j.json.DockerResponseNode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;

import java.net.URI;

public class StartContainerHandler extends DockerHandler {
    public StartContainerHandler(Builder builder) {
        super(builder);
    }

    @Override
    public FullHttpRequest render() {
        DockerResponse lastRes = node.last();
        String containerId = lastRes.getContainerId();
        URI uri = EndpointUtil.startContainer(containerId);
        logger.info("Rendered FullHttpRequest. URL == {}", uri);
        return RequestHelper.post(uri, false, null, null);
    }

    @Override
    protected DockerResponse parseResponseBody(String json) throws JsonProcessingException {
        /*This method doesn't parse and store the json string since the http response doesn't have body message*/
        return null;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        node = (DockerResponseNode) evt;
        FullHttpRequest req = render();
        ctx.channel().writeAndFlush(req);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpResponse res) throws Exception {
        if (res.status().code() == 204 || res.status().code() == 304) {
            checkLast(ctx);
        } else {
            String errMessage = String.format("Unsuccessful response detected: %s %s",
                    res.status().toString(),
                    res.content().toString(CharsetUtil.UTF_8));
            throw new DockerResponseException(errMessage);
        }
    }

    public static class Builder implements DockerRequestBuilder {
        @Override
        public DockerHandler build() {
            return new StartContainerHandler(this);
        }
    }
}
