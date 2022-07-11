package client.docker;

import client.docker.model.exec.ExecCreateConfig;
import client.docker.exceptions.DockerRequestException;
import client.docker.internal.http.URIs;
import client.docker.internal.http.RequestHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.CharsetUtil;

import java.net.URI;

public class ExecCreateRequest extends DockerRequest {
    private final ExecCreateConfig config;

    public ExecCreateRequest(Builder builder) {
        super(builder);
        this.config = builder.config;
    }

    @Override
    public FullHttpRequest render() {
        try {
            byte[] body = JacksonHelper.writeValueAsString(config).getBytes(CharsetUtil.UTF_8);
            String containerId = node.find("_container_id");
            URI uri = URIs.EXEC_CREATE.uri(containerId);
            ByteBuf bodyBuffer = allocator.heapBuffer().writeBytes(body);
            logger.debug("Rendered FullHttpRequest. URL == {}", uri);
            return RequestHelper.post(uri, true, bodyBuffer, HttpHeaderValues.APPLICATION_JSON);
        } catch (JsonProcessingException e) {
            String errMsg = String.format("Exception raised while build the %s command", this.getClass().getSimpleName());
            throw new DockerRequestException(errMsg, e);
        }
    }

    @Override
    protected ChannelInboundHandlerAdapter handler() {
        return new ExecCreateHandler().setNextRequest(nextRequest)
                .setNode(node)
                .setAllocator(allocator)
                .setPromise(promise);
    }

    public static class Builder implements DockerRequestBuilder {
        private final ExecCreateConfig config = new ExecCreateConfig();

        public Builder withAttachStdin(boolean attachStdin) {
            config.setAttachStdin(attachStdin);
            return this;
        }

        public Builder withAttachStdout(boolean attachStdout) {
            config.setAttachStdout(attachStdout);
            return this;
        }

        public Builder withAttachStderr(boolean attachStderr) {
            config.setAttachStderr(attachStderr);
            return this;
        }

        public Builder withDetachKeys(String detachKeys) {
            config.setDetachKeys(detachKeys);
            return this;
        }

        public Builder withTty(boolean tty) {
            config.setTty(tty);
            return this;
        }

        public Builder withEnv(String[] env) {
            config.setEnv(env);
            return this;
        }

        public Builder withCmd(String[] cmd) {
            config.setCmd(cmd);
            return this;
        }

        public Builder withPrivileged(boolean privileged) {
            config.setPrivileged(privileged);
            return this;
        }

        public Builder withUser(String user) {
            config.setUser(user);
            return this;
        }

        public Builder withWorkingDir(String workingDir) {
            config.setWorkingDir(workingDir);
            return this;
        }

        @Override
        public ExecCreateRequest build() {
            return new ExecCreateRequest(this);
        }
    }
}
