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

public class RemoveContainerHandler extends DockerHandler {
    private final boolean v;
    private final boolean force;
    private final boolean link;

    public RemoveContainerHandler(Builder builder) {
        super(builder);
        this.v = builder.v;
        this.force = builder.force;
        this.link = builder.link;
    }

    @Override
    public FullHttpRequest render() throws DockerRequestException {
        DockerResponse lastRes = node.last();
        String containerId = lastRes.getContainerId();
        URI uri = EndpointUtil.removeContainer(containerId, v, force, link);
        logger.debug("Rendered FullHttpRequest. URL == {}", uri);
        return RequestHelper.delete(uri);
    }

    @Override
    protected DockerResponse parseResponseBody(String json) throws JsonProcessingException {
        //node.setResponse(JacksonHelper.toNode(json));
        return null;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        this.node = (DockerResponseNode) evt;
        FullHttpRequest req = render();
        ctx.channel().writeAndFlush(req);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpResponse res) throws Exception {
        if (res.status().code() == 204) {
            checkLast(ctx);
        } else {
            String errMessage = String.format("Unsuccessful response detected: %s %s",
                    res.status().toString(),
                    res.content().toString(CharsetUtil.UTF_8));
            throw new DockerResponseException(errMessage);
        }
    }

    public static class Builder implements DockerRequestBuilder {
        private boolean v = false;
        private boolean force = false;
        private boolean link = false;

        public Builder withVolumeRemove(boolean v) {
            this.v = v;
            return this;
        }

        public Builder withForceRemove(boolean force) {
            this.force = force;
            return this;
        }

        public Builder withLinkRemove(boolean link) {
            this.link = link;
            return this;
        }

        @Override
        public DockerHandler build() {
            return new RemoveContainerHandler(this);
        }
    }
}
