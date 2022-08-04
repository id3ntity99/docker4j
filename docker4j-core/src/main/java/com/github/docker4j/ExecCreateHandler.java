package com.github.docker4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.docker4j.exceptions.DockerRequestException;
import com.github.docker4j.exceptions.DockerResponseException;
import com.github.docker4j.internal.http.EndpointUtil;
import com.github.docker4j.internal.http.RequestHelper;
import com.github.docker4j.json.DockerResponseNode;
import com.github.docker4j.json.JacksonHelper;
import com.github.docker4j.model.exec.ExecCreateConfig;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.CharsetUtil;

import java.net.URI;

public class ExecCreateHandler extends DockerHandler {
    private final ExecCreateConfig config;
    private final String containerId;

    private ExecCreateHandler(Builder builder) {
        super(builder);
        this.config = builder.config;
        this.containerId = builder.containerId;
    }

    @Override
    public FullHttpRequest render() {
        try {
            byte[] body = JacksonHelper.writeValueAsString(config).getBytes(CharsetUtil.UTF_8);
            if (this.containerId == null) {
                DockerResponse lastRes = node.last();
                String id = lastRes.getContainerId();
                URI uri = EndpointUtil.execCreate(id);
                ByteBuf bodyBuffer = allocator.heapBuffer().writeBytes(body);
                logger.info("Rendered FullHttpRequest. URL == {}", uri);
                return RequestHelper.post(uri, true, bodyBuffer, HttpHeaderValues.APPLICATION_JSON);
            } else {
                URI uri = EndpointUtil.execCreate(containerId);
                ByteBuf bodyBuffer = allocator.heapBuffer().writeBytes(body);
                logger.info("Rendered FullHttpRequest. URL == {}", uri);
                return RequestHelper.post(uri, true, bodyBuffer, HttpHeaderValues.APPLICATION_JSON);
            }
        } catch (JsonProcessingException e) {
            String errMsg = String.format("Exception raised while build the %s command", this.getClass().getSimpleName());
            throw new DockerRequestException(errMsg, e);
        }
    }

    @Override
    protected DockerResponse parseResponseBody(String json) throws JsonProcessingException {
        DockerResponse execCreateRes = mapper.readValue(json, ExecCreateResponse.class);
        node.add(execCreateRes);
        return execCreateRes;
        //String execId = JacksonHelper.getValue("Id", json);
        //node.setResponse(JacksonHelper.toNode(json));
        //node.setInternal(DockerResponseNode.EXEC_ID, execId);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        this.node = (DockerResponseNode) evt;
        FullHttpRequest req = render();
        ctx.channel().writeAndFlush(req);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpResponse res) throws Exception {
        if (res.status().code() == 201) {
            String json = res.content().toString(CharsetUtil.UTF_8);
            parseResponseBody(json);
            checkLast(ctx);
        } else {
            String errMessage = String.format("Unsuccessful response detected: %s %s",
                    res.status().toString(),
                    res.content().toString(CharsetUtil.UTF_8));
            throw new DockerResponseException(errMessage);
        }
    }


    public static class Builder implements DockerRequestBuilder {
        private final ExecCreateConfig config = new ExecCreateConfig();
        private String containerId;

        public Builder withContainerId(String containerId) {
            this.containerId = containerId;
            return this;
        }

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
