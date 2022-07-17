package com.github.docker4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.docker4j.internal.http.EndpointUtil;
import com.github.docker4j.json.DockerResponseNode;
import com.github.docker4j.json.JacksonHelper;
import com.github.docker4j.exceptions.DockerRequestException;
import com.github.docker4j.exceptions.DockerResponseException;
import com.github.docker4j.internal.http.RequestHelper;
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
            String execId = node.getInternal(DockerResponseNode.EXEC_ID);
            URI uri = EndpointUtil.execStart(execId);
            byte[] body = JacksonHelper.writeValueAsString(config).getBytes(CharsetUtil.UTF_8);
            ByteBuf bodyBuffer = allocator.heapBuffer().writeBytes(body);
            FullHttpRequest req = RequestHelper.post(uri, true, bodyBuffer, HttpHeaderValues.APPLICATION_JSON);
            req.headers().set(HttpHeaderNames.UPGRADE, "tcp");
            return req;
        } catch (JsonProcessingException e) {
            String errMsg = String.format("Exception raised while build the %s command", this.getClass().getSimpleName());
            throw new DockerRequestException(errMsg, e);
        }
    }

    @Override
    protected void parseResponseBody(String json) throws JsonProcessingException {
        node.setResponse(JacksonHelper.toNode(json));
    }

    //FIXME 다른 Handler들처럼 리팩터링.
    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpResponse res) throws Exception {
        if (res.status().code() == 101) {
            String json = res.content().toString(CharsetUtil.UTF_8);
            parseResponseBody(json);
            //ctx.pipeline().remove(HttpClientCodec.class);
            //ctx.pipeline().remove(HttpObjectAggregator.class);
            //ctx.pipeline().remove(this);
            ctx.pipeline().addLast(new DockerFrameDecoder(input));
            ctx.fireChannelActive();
        } else {
            String errMessage = String.format("Unsuccessful response detected: %s %s",
                    res.status().toString(),
                    res.content().toString(CharsetUtil.UTF_8));
            node.setInternal(DockerResponseNode.ERROR, errMessage);
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
