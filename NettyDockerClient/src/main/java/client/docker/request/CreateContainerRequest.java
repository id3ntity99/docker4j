package client.docker.request;

import client.docker.configs.config.Config;
import client.docker.configs.config.ExposedPorts;
import client.docker.configs.config.HealthConfig;
import client.docker.configs.config.Volumes;
import client.docker.configs.hostconfig.HostConfig;
import client.docker.configs.hostconfig.networkingconfig.NetworkingConfig;
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
import java.util.Map;

public class CreateContainerRequest extends DockerRequest {
    private final Config config;

    public CreateContainerRequest(Builder builder) {
        super(builder);
        this.config = builder.config;
    }

    @Override
    protected CreateContainerRequest setContainer(Container container) {
        this.container = container;
        return this;
    }

    @Override
    protected DockerRequest setNextRequest(DockerRequest nextRequest) {
        this.nextRequest = nextRequest;
        return this;
    }

    @Override
    protected DockerRequest setAllocator(ByteBufAllocator allocator) {
        this.allocator = allocator;
        return this;
    }

    @Override
    public FullHttpRequest render() {
        try {
            byte[] body = writer.writeValueAsString(config).getBytes(CharsetUtil.UTF_8);
            URI uri = URIs.CREATE_CONTAINER.uri();
            ByteBuf bodyBuffer = allocator.heapBuffer().writeBytes(body);
            logger.debug(String.format("Rendered FullHttpRequest. URL == %s", uri));
            return RequestHelper.post(uri, true, bodyBuffer, HttpHeaderValues.APPLICATION_JSON);
        } catch (JsonProcessingException e) {
            String errMsg = String.format("Exception raised while build the %s command", this.getClass().getSimpleName());
            throw new DockerRequestException(errMsg, e);
        }
    }

    @Override
    public DockerResponseHandler handler() {
        return new CreateContainerResponseHandler(container, nextRequest);
    }

    public static class Builder implements DockerRequest.Builder {
        private final Config config = new Config();

        public Builder withHostConfig(HostConfig hostConfig) {
            config.setHostConfig(hostConfig);
            return this;
        }

        public Builder withNetworkingConfig(NetworkingConfig networkingConfig) {
            config.setNetworkingConfig(networkingConfig);
            return this;
        }

        public Builder withHostname(String hostname) {
            config.setHostName(hostname);
            return this;
        }

        public Builder withDomainName(String domainName) {
            config.setDomainName(domainName);
            return this;
        }

        public Builder withUser(String user) {
            config.setUser(user);
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

        public Builder withTty(boolean tty) {
            config.setTty(tty);
            return this;
        }

        public Builder withOpenStdin(boolean openStdin) {
            config.setOpenStdin(openStdin);
            return this;
        }

        public Builder withStdinOnce(boolean stdinOnce) {
            config.setStdinOnce(stdinOnce);
            return this;
        }

        public Builder withExposedPorts(ExposedPorts exposedPorts) {
            config.setExposedPorts(exposedPorts);
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

        public Builder withHealthConfig(HealthConfig healthConfig) {
            config.setHealthConfig(healthConfig);
            return this;
        }

        public Builder withArgsEscaped(boolean argsEscaped) {
            config.setArgsEscaped(argsEscaped);
            return this;
        }

        public Builder withImage(String image) {
            config.setImage(image);
            return this;
        }

        public Builder withVolumes(Volumes volumes) {
            config.setVolumes(volumes);
            return this;
        }

        public Builder withWorkingDir(String workingDir) {
            config.setWorkingDir(workingDir);
            return this;
        }

        public Builder withEntryPoint(String[] entryPoint) {
            config.setEntryPoint(entryPoint);
            return this;
        }

        public Builder withNetworkDisabled(boolean networkDisabled) {
            config.setNetworkDisabled(networkDisabled);
            return this;
        }

        public Builder withMacAddress(String macAddress) {
            config.setMacAddress(macAddress);
            return this;
        }

        public Builder withOnBuild(String[] onBuild) {
            config.setOnBuild(onBuild);
            return this;
        }

        public Builder withLabels(Map<String, String> labels) {
            config.setLabels(labels);
            return this;
        }

        public Builder withStopSignal(String stopSignal) {
            config.setStopSignal(stopSignal);
            return this;
        }

        public Builder withStopTimeout(int stopTimeout) {
            config.setStopTimeout(stopTimeout);
            return this;
        }

        public Builder withShell(String[] shell) {
            config.setShell(shell);
            return this;
        }

        @Override
        public DockerRequest build() {
            return new CreateContainerRequest(this);
        }
    }

    private static class CreateContainerResponseHandler extends DockerResponseHandler {
        public CreateContainerResponseHandler(Container container, DockerRequest nextRequest) {
            super(container, nextRequest);
            this.container = container;
            this.nextRequest = nextRequest;
        }

        private void handleResponse(ChannelHandlerContext ctx, FullHttpResponse res) throws Exception {
            String jsonBody = res.content().toString(CharsetUtil.UTF_8);
            String containerId = mapper.readTree(jsonBody).get("Id").asText();
            container.setContainerId(containerId);
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
                            ctx.pipeline().replace(CreateContainerResponseHandler.class, nextHandler.toString(), nextHandler);
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
            if (res.status().code() == 201)
                handleResponse(ctx, res);
            else {
                String errMessage = String.format("Unsuccessful response detected: %s %s",
                        res.status().toString(),
                        res.content().toString(CharsetUtil.UTF_8));
                throw new DockerResponseException(errMessage);
            }
        }
    }
}
