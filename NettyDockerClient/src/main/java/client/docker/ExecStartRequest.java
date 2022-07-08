package client.docker;

import client.docker.model.exec.ExecStartConfig;
import client.docker.internal.http.URIs;
import client.docker.internal.http.RequestHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.CharsetUtil;

import java.net.URI;

public class ExecStartRequest extends DockerRequest {
    private final Channel input;
    private final ExecStartConfig config;

    public ExecStartRequest(Builder builder) {
        super(builder);
        input = builder.input;
        config = builder.config;
    }

    @Override
    public FullHttpRequest render() throws Exception {
        String execId = container.getExecId();
        URI uri = URIs.EXEC_START.uri(execId);
        byte[] body = writer.writeValueAsString(config).getBytes(CharsetUtil.UTF_8);
        ByteBuf bodyBuffer = allocator.heapBuffer().writeBytes(body);
        FullHttpRequest req = RequestHelper.post(uri, true, bodyBuffer, HttpHeaderValues.APPLICATION_JSON);
        req.headers().set(HttpHeaderNames.UPGRADE, "tcp");
        return req;
    }

    @Override
    protected ChannelInboundHandlerAdapter handler() {
        return new DockerFrameDecoder(input);
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
        public DockerRequest build() {
            return new ExecStartRequest(this);
        }
    }
}
