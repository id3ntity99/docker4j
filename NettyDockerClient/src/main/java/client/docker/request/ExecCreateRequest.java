package client.docker.request;

import client.docker.configs.exec.ExecCreateConfig;
import client.docker.dockerclient.exceptions.DockerResponseException;
import client.docker.model.Container;
import client.docker.request.exceptions.DockerRequestException;
import client.docker.uris.URIs;
import client.docker.util.RequestHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
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
    protected ExecCreateRequest setContainer(Container container) {
        this.container = container;
        return this;
    }

    @Override
    protected ExecCreateRequest setNextRequest(DockerRequest nextRequest) {
        this.nextRequest = nextRequest;
        return this;
    }

    @Override
    protected DockerRequest setAllocator(ByteBufAllocator allocator) {
        this.allocator = allocator;
        return this;
    }

    @Override
    public DockerResponseHandler handler() {
        return new ExecCreateResponseHandler(container, nextRequest);
    }


    @Override
    public FullHttpRequest render() throws Exception {
        try {
            byte[] body = writer.writeValueAsString(config).getBytes(CharsetUtil.UTF_8);
            String containerId = container.getContainerId();
            URI uri = URIs.EXEC_CREATE.uri(containerId);
            ByteBuf bodyBuffer = allocator.heapBuffer().writeBytes(body);
            logger.debug(String.format("Rendered FullHttpRequest. URL == %s", uri));
            return RequestHelper.post(uri, true, bodyBuffer, HttpHeaderValues.APPLICATION_JSON);
        } catch (JsonProcessingException e) {
            String errMsg = String.format("Exception raised while build the %s command", this.getClass().getSimpleName());
            throw new DockerRequestException(errMsg, e);
        }
    }

    public static class Builder implements DockerRequest.Builder {
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

    private static class ExecCreateResponseHandler extends DockerResponseHandler {
        public ExecCreateResponseHandler(Container container, DockerRequest nextRequest) {
            super(container, nextRequest);
            this.container = container;
            this.nextRequest = nextRequest;
        }

        private void handleResponse(ChannelHandlerContext ctx, FullHttpResponse res) throws Exception {
            String jsonBody = res.content().toString(CharsetUtil.UTF_8);
            String execId = mapper.readTree(jsonBody).get("Id").asText();
            container.setExecId(execId);
            if (nextRequest != null) {
                logger.debug(String.format("Next request detected: %s", nextRequest.getClass().getSimpleName()));
                nextRequest.setContainer(container);
                FullHttpRequest nextHttpReq = nextRequest.render();
                ctx.channel().writeAndFlush(nextHttpReq).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            logger.debug(String.format("Successfully sent %s", nextRequest.getClass().getSimpleName()));
                            DockerResponseHandler nextHandler = nextRequest.handler();
                            ctx.pipeline().replace(ExecCreateResponseHandler.class, nextHandler.toString(), nextHandler);
                        } else {
                            String errMsg = String.format("Exception raised while sending next %s", nextRequest.getClass().getSimpleName());
                            logger.error(errMsg, future.cause());
                        }
                    }
                });
            } else {
                logger.info(String.format("There are no more requests... removing %s", this.getClass().getSimpleName()));
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
    }
}
