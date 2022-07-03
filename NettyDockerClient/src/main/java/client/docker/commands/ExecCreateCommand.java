package client.docker.commands;

import client.docker.commands.exceptions.DockerRequestException;
import client.docker.configs.exec.ExecCreateConfig;
import client.docker.dockerclient.NettyDockerClient;
import client.docker.uris.URIs;
import client.docker.util.RequestHelper;
import client.docker.model.SimpleResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderValues;
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

    public ExecCreateCommand withContainerId(String containerId) {
        this.containerId = containerId;
        return this;
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
            URI uri = new URI(URIs.EXEC_CREATE.uri(containerId));
            String body = writer.writeValueAsString(config);
            ByteBuf bodyBuffer = Unpooled.copiedBuffer(body, CharsetUtil.UTF_8);
            FullHttpRequest req = RequestHelper.post(uri, true, bodyBuffer, HttpHeaderValues.APPLICATION_JSON);
            SimpleResponse res = nettyDockerClient.request(req).sync().get();
            return mapper.readTree(res.getBody()).get("Id").asText();
        } catch (RuntimeException | ExecutionException | JsonProcessingException | URISyntaxException e) {
            String errMsg = String.format("Exception raised while building command %s", this.getClass().getSimpleName());
            throw new DockerRequestException(errMsg, e);
        } catch (InterruptedException e) {
            String errMsg = String.format("Exception raised while building command %s", this.getClass().getSimpleName());
            Thread.currentThread().interrupt();
            throw new DockerRequestException(errMsg, e);
        }
    }
}
