package com.github.docker4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.docker4j.exceptions.DockerResponseException;
import com.github.docker4j.internal.http.EndpointUtil;
import com.github.docker4j.internal.http.RequestHelper;
import com.github.docker4j.json.DockerResponseNode;
import com.github.docker4j.json.JacksonHelper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;

import java.net.URI;

public class StopContainerHandler extends DockerHandler {
    private final int waitTime;

    public StopContainerHandler(Builder builder) {
        super(builder);
        this.waitTime = builder.waitTime;
    }

    @Override
    public FullHttpRequest render() {
        String containerId = node.getInternal(DockerResponseNode.CONTAINER_ID);
        URI uri = EndpointUtil.stopContainer(containerId, waitTime);
        logger.debug("Rendered FullHttpRequest. URL == {}", uri);
        return RequestHelper.post(uri, false, null, null);
    }

    @Override
    protected void parseResponseBody(String json) throws JsonProcessingException {
        node.setResponse(JacksonHelper.toNode(json));
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpResponse res) throws Exception {
        if (res.status().code() == 204 || res.status().code() == 304) {
            String json = res.content().toString(CharsetUtil.UTF_8);
            parseResponseBody(json);
            handleResponse(ctx);
        } else {
            String errMessage = String.format("Unsuccessful response detected: %s %s",
                    res.status().toString(),
                    res.content().toString(CharsetUtil.UTF_8));
            node.setInternal(DockerResponseNode.ERROR, errMessage);
            throw new DockerResponseException(errMessage);
        }

    }

    public static class Builder implements DockerRequestBuilder {
        private int waitTime;

        public Builder withWaitTime(int waitTime) {
            this.waitTime = waitTime;
            return this;
        }

        @Override
        public DockerHandler build() {
            return new StopContainerHandler(this);
        }
    }
}
