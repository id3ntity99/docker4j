package com.github.docker4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.docker4j.exceptions.DockerRequestException;
import com.github.docker4j.exceptions.DockerResponseException;
import com.github.docker4j.internal.http.EndpointUtil;
import com.github.docker4j.internal.http.RequestHelper;
import com.github.docker4j.json.DockerResponseNode;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;

import java.net.URI;

public class InspectContainerHandler extends DockerHandler {
    private final boolean size;
    private final String containerId;

    private InspectContainerHandler(Builder builder) {
        super(builder);
        this.size = builder.size;
        this.containerId = builder.containerId;
    }

    @Override
    public FullHttpRequest render() throws DockerRequestException {
        if (this.containerId == null) {
            DockerResponse lastRes = node.last();
            String id = lastRes.getContainerId();
            URI uri = EndpointUtil.inspectContainer(id, size);
            return RequestHelper.get(uri);
        } else {
            URI uri = EndpointUtil.inspectContainer(this.containerId, size);
            return RequestHelper.get(uri);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        node = (DockerResponseNode) evt;
        FullHttpRequest req = render();
        ctx.channel().writeAndFlush(req);
    }

    @Override
    protected DockerResponse parseResponseBody(String json) throws JsonProcessingException {
        //String containerId = JacksonHelper.getValue("Id", json);
        //node.setResponse(JacksonHelper.toNode(json));
        return mapper.readValue(json, InspectResponse.class);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpResponse res) throws Exception {
        if (res.status().code() == 200) {
            String json = res.content().toString(CharsetUtil.UTF_8);
            DockerResponse inspectRes = parseResponseBody(json);
            checkLast(ctx, inspectRes);
        } else {
            String errMessage = String.format("Unsuccessful response detected: %s %s",
                    res.status().toString(),
                    res.content().toString(CharsetUtil.UTF_8));
            throw new DockerResponseException(errMessage);
        }
    }


    public static class Builder implements DockerRequestBuilder {
        private boolean size;
        private String containerId;

        public Builder withSize(boolean size) {
            this.size = size;
            return this;
        }

        public Builder withContainerId(String containerId) {
            this.containerId = containerId;
            return this;
        }

        @Override
        public DockerHandler build() {
            return new InspectContainerHandler(this);
        }
    }
}
