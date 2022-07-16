package client.docker;

import client.docker.exceptions.DockerRequestException;
import client.docker.exceptions.DockerResponseException;
import client.docker.internal.http.RequestHelper;
import client.docker.internal.http.URIs;
import client.docker.model.exec.ExecCreateConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.CharsetUtil;

import java.net.URI;

public class ExecCreateRequestHandler extends DockerRequestHandler {
    private final ExecCreateConfig config;

    public ExecCreateRequestHandler(Builder builder) {
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

    private void parseResponseBody(String json) throws JsonProcessingException {
        JsonNode jsonNode = JacksonHelper.toNode(json);
        String execId = jsonNode.get("Id").asText();
        node.add("exec_create_response", json);
        node.add("_exec_id", execId);
    }

    private void handleResponse(ChannelHandlerContext ctx, FullHttpResponse res) throws Exception {
        String json = res.content().toString(CharsetUtil.UTF_8);
        parseResponseBody(json);
        if (nextRequest != null) {
            logger.debug("Next request detected {}", nextRequest.getClass().getSimpleName());
            handOver();
            FullHttpRequest nextHttpReq = nextRequest.render();
            ctx.channel().writeAndFlush(nextHttpReq).addListener(new NextRequestListener(ctx, this, nextRequest));
        } else {
            logger.info("There are no more requests... removing {}", this.getClass().getSimpleName());
            promise.setSuccess(node);
            ctx.pipeline().remove(this);
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpResponse res) throws Exception {
        if (res.status().code() == 201) {
            handleResponse(ctx, res);
        } else {
            String errMessage = String.format("Unsuccessful response detected: %s %s",
                    res.status().toString(),
                    res.content().toString(CharsetUtil.UTF_8));
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
        public ExecCreateRequestHandler build() {
            return new ExecCreateRequestHandler(this);
        }
    }
}
