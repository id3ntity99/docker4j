package docker4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import docker4j.exceptions.DockerRequestException;
import docker4j.exceptions.DockerResponseException;
import docker4j.internal.http.EndpointUtil;
import docker4j.internal.http.RequestHelper;
import docker4j.json.DockerResponseNode;
import docker4j.json.JacksonHelper;
import docker4j.model.exec.ExecCreateConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.CharsetUtil;

import java.net.URI;

public class ExecCreateHandler extends DockerHandler {
    private final ExecCreateConfig config;

    public ExecCreateHandler(Builder builder) {
        super(builder);
        this.config = builder.config;
    }

    @Override
    public FullHttpRequest render() {
        try {
            byte[] body = JacksonHelper.writeValueAsString(config).getBytes(CharsetUtil.UTF_8);
            String containerId = node.getInternal(DockerResponseNode.CONTAINER_ID);
            URI uri = EndpointUtil.execCreate(containerId);
            ByteBuf bodyBuffer = allocator.heapBuffer().writeBytes(body);
            logger.debug("Rendered FullHttpRequest. URL == {}", uri);
            return RequestHelper.post(uri, true, bodyBuffer, HttpHeaderValues.APPLICATION_JSON);
        } catch (JsonProcessingException e) {
            String errMsg = String.format("Exception raised while build the %s command", this.getClass().getSimpleName());
            throw new DockerRequestException(errMsg, e);
        }
    }

    @Override
    protected void parseResponseBody(String json) throws JsonProcessingException {
        String execId = JacksonHelper.getValue("Id", json);
        node.setResponse(JacksonHelper.toNode(json));
        node.setInternal(DockerResponseNode.EXEC_ID, execId);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpResponse res) throws Exception {
        if (res.status().code() == 201) {
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
        public ExecCreateHandler build() {
            return new ExecCreateHandler(this);
        }
    }
}