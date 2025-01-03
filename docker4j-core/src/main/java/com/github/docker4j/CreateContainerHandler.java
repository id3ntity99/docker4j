package com.github.docker4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.docker4j.exceptions.DockerRequestException;
import com.github.docker4j.exceptions.DockerResponseException;
import com.github.docker4j.internal.http.EndpointUtil;
import com.github.docker4j.internal.http.RequestHelper;
import com.github.docker4j.json.DockerResponseNode;
import com.github.docker4j.json.JacksonHelper;
import com.github.docker4j.model.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.CharsetUtil;

import java.net.URI;
import java.util.Map;

public class CreateContainerHandler extends DockerHandler {
    private final Config config;

    public CreateContainerHandler(Builder builder) {
        super(builder);
        this.config = builder.config;
    }

    @Override
    public FullHttpRequest render() {
        try {
            byte[] body = JacksonHelper.writeValueAsString(config).getBytes(CharsetUtil.UTF_8);
            URI uri = EndpointUtil.createContainer();
            ByteBuf bodyBuffer = allocator.heapBuffer().writeBytes(body);
            logger.debug("Rendered FullHttpRequest. URL == {}", uri);
            return RequestHelper.post(uri, true, bodyBuffer, HttpHeaderValues.APPLICATION_JSON);
        } catch (JsonProcessingException e) {
            String errMsg = String.format("Exception raised while build the %s command", this.getClass().getSimpleName());
            throw new DockerRequestException(errMsg, e);
        }
    }

    @Override
    protected DockerResponse parseResponseBody(String json) throws JsonProcessingException {
        return mapper.readValue(json, CreateContainerResponse.class);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        this.node = (DockerResponseNode) evt;
        ctx.channel().writeAndFlush(render());
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpResponse res) throws Exception {
        if (res.status().code() == 201) {
            String json = res.content().toString(CharsetUtil.UTF_8);
            DockerResponse createResponse = parseResponseBody(json);
            checkLast(ctx, createResponse);
        } else {
            String errMessage = String.format("Unsuccessful response detected: %s %s",
                    res.status().toString(),
                    res.content().toString(CharsetUtil.UTF_8));
            throw new DockerResponseException(errMessage);
        }
    }

    public static class Builder implements DockerRequestBuilder {
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
        public DockerHandler build() {
            return new CreateContainerHandler(this);
        }
    }
}
