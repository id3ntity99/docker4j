package client.docker;

import client.docker.exceptions.DockerRequestException;
import client.docker.internal.http.RequestHelper;
import client.docker.internal.http.URIs;
import client.docker.model.exec.ExecStartConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;

public class ExecStartRequestHandler extends DockerRequestHandler {
    private final Channel input;
    private final ExecStartConfig config;

    public ExecStartRequestHandler(Builder builder) {
        super(builder);
        input = builder.input;
        config = builder.config;
    }

    @Override
    public FullHttpRequest render() throws DockerRequestException {
        try {
            String execId = node.find("_exec_id");
            URI uri = URIs.EXEC_START.uri(execId);
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
    public void channelRead0(ChannelHandlerContext ctx, FullHttpResponse res) throws Exception {
        if (res.status().code() == 101) {
            ctx.pipeline().remove(HttpClientCodec.class);
            ctx.pipeline().remove(HttpObjectAggregator.class);
            ctx.pipeline().remove(this);
            ctx.pipeline().addLast(new DockerFrameDecoder(input));
            ctx.fireChannelActive();
        } else {
            System.out.println("Something went wrong");
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
        public DockerRequestHandler build() {
            return new ExecStartRequestHandler(this);
        }
    }
}
