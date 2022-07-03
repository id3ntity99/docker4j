package client.docker.commands;

import client.docker.commands.exceptions.DockerRequestException;
import client.docker.configs.exec.ExecCreateConfig;
import client.docker.dockerclient.proxy.NettyDockerClient;
import client.nettyserver.SimpleResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

public class ExecCreateCommand extends Command<ExecCreateCommand, String> {
    private final ExecCreateConfig config = new ExecCreateConfig();
    private String containerId;
    private NettyDockerClient nettyDockerClient;

    @Override
    public ExecCreateCommand withDockerClient(NettyDockerClient nettyDockerClient) {
        this.nettyDockerClient = nettyDockerClient;
        return this;
    }

    public ExecCreateCommand(String containerId) {
        this.containerId = containerId;
    }

    public ExecCreateCommand withAttachStdin(boolean attachStdin) {
        config.setAttachStdin(attachStdin);
        return this;
    }

    public ExecCreateCommand withAttachStdout(boolean attachStdout) {
        config.setAttachStdout(attachStdout);
        return this;
    }

    public ExecCreateCommand withAttachStderr(boolean attachStderr) {
        config.setAttachStderr(attachStderr);
        return this;
    }

    public ExecCreateCommand withDetachKeys(String detachKeys) {
        config.setDetachKeys(detachKeys);
        return this;
    }

    public ExecCreateCommand withTty(boolean tty) {
        config.setTty(tty);
        return this;
    }

    public ExecCreateCommand withEnv(String[] env) {
        config.setEnv(env);
        return this;
    }

    public ExecCreateCommand withCmd(String[] cmd) {
        config.setCmd(cmd);
        return this;
    }

    public ExecCreateCommand withPrivileged(boolean privileged) {
        config.setPrivileged(privileged);
        return this;
    }

    public ExecCreateCommand withUser(String user) {
        config.setUser(user);
        return this;
    }

    public ExecCreateCommand withWorkingDir(String workingDir) {
        config.setWorkingDir(workingDir);
        return this;
    }

    @Override
    public String exec() throws DockerRequestException {
        try {
            String stringUri = String.format("http://localhost:2375/containers/%s/exec", containerId);
            URI uri = new URI(stringUri);
            String body = writer.writeValueAsString(config);
            ByteBuf bodyBuffer = Unpooled.copiedBuffer(body, CharsetUtil.UTF_8);
            FullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getRawPath());
            req.headers().set(HttpHeaderNames.HOST, uri.getHost());
            req.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            req.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
            req.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
            req.content().writeBytes(bodyBuffer);
            req.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
            req.headers().set(HttpHeaderNames.CONTENT_LENGTH, bodyBuffer.readableBytes());
            SimpleResponse res = nettyDockerClient.request(req).sync().get();
            return mapper.readTree(res.getBody()).get("Id").asText();
        } catch (RuntimeException | ExecutionException | JsonProcessingException | URISyntaxException e) {
            String errMsg = String.format("Exception raised while building command %s", this.getClass().getSimpleName());
            throw new client.docker.dockerclient.proxy.exceptions.DockerRequestException(errMsg, e);
        } catch (InterruptedException e) {
            String errMsg = String.format("Exception raised while building command %s", this.getClass().getSimpleName());
            Thread.currentThread().interrupt();
            throw new client.docker.dockerclient.proxy.exceptions.DockerRequestException(errMsg, e);
        }
    }
}
