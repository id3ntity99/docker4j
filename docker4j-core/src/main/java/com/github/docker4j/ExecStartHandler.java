package com.github.docker4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.docker4j.exceptions.DockerRequestException;
import com.github.docker4j.exceptions.DockerResponseException;
import com.github.docker4j.internal.http.EndpointUtil;
import com.github.docker4j.internal.http.RequestHelper;
import com.github.docker4j.json.DockerResponseNode;
import com.github.docker4j.json.JacksonHelper;
import com.github.docker4j.model.exec.ExecStartConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

public class ExecStartHandler extends DockerHandler {
    private final Channel input;
    private final ExecStartConfig config;

    public ExecStartHandler(Builder builder) {
        super(builder);
        input = builder.input;
        config = builder.config;
    }

    @Override
    public FullHttpRequest render() throws DockerRequestException {
        try {
            //String execId = node.getInternal(DockerResponseNode.EXEC_ID);
            DockerResponse lastResponse = node.last();
            String execId = lastResponse.getExecIds()[0];
            URI uri = EndpointUtil.execStart(execId);
            byte[] body = JacksonHelper.writeValueAsString(config).getBytes(CharsetUtil.UTF_8);
            ByteBuf bodyBuffer = allocator.heapBuffer().writeBytes(body);
            FullHttpRequest req = RequestHelper.post(uri, true, bodyBuffer, HttpHeaderValues.APPLICATION_JSON);
            req.headers().set(HttpHeaderNames.UPGRADE, "tcp");
            logger.info("Rendered FullHttpRequest. URL == {}", uri);
            return req;
        } catch (JsonProcessingException e) {
            String errMsg = String.format("Exception raised while build the %s command", this.getClass().getSimpleName());
            throw new DockerRequestException(errMsg, e);
        }
    }

    @Override
    protected DockerResponse parseResponseBody(String json) throws JsonProcessingException {
        /*This method returns null since the response body is empty*/
        return null;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        this.node = (DockerResponseNode) evt;
        FullHttpRequest req = render();
        ctx.channel().writeAndFlush(req);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpResponse res) throws Exception {
        if (res.status().code() == 101) {
            promise.setSuccess(node);
            ctx.channel().pipeline().remove(HttpClientCodec.class);
            ctx.channel().pipeline().remove(HttpObjectAggregator.class);
            ctx.channel().pipeline().remove(this);
            ctx.channel().pipeline().addLast(new DockerFrameDecoder(input));
            logger.debug("Remaining DockerClient's handlers: {}", ctx.pipeline().toMap());
            ctx.fireChannelActive();
        } else {
            String errMessage = String.format("Unsuccessful response detected: %s %s",
                    res.status().toString(),
                    res.content().toString(CharsetUtil.UTF_8));
            throw new DockerResponseException(errMessage);
        }
    }

    public static class Builder implements DockerRequestBuilder {
        private Channel input;
        private final ExecStartConfig config = new ExecStartConfig();

        public Builder withInput(Channel input) {
            this.input = input;
            return this;
        }

        public Builder withDetach(boolean detach) {
            config.setDetach(detach);
            return this;
        }

        public Builder withTty(boolean tty) {
            config.setTty(tty);
            return this;
        }

        @Override
        public DockerHandler build() {
            return new ExecStartHandler(this);
        }
    }
}
