package client.docker.commands;

import client.docker.commands.exceptions.DockerRequestException;
import client.docker.configs.config.Config;
import client.docker.configs.config.ExposedPorts;
import client.docker.configs.config.HealthConfig;
import client.docker.configs.config.Volumes;
import client.docker.configs.hostconfig.HostConfig;
import client.docker.configs.hostconfig.networkingconfig.NetworkingConfig;
import client.docker.dockerclient.NettyDockerClient;
import client.docker.model.Container;
import client.docker.util.RequestHelper;
import client.docker.model.SimpleResponse;
import client.docker.uris.URIs;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.util.CharsetUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Create config and request Docker daemon for creating a container.
 * Since this class creates {@link Config}, user doesn't have to create {@link Config} separately.
 * All the user has to do is just using the methods of this class to configure the {@link Config},
 * and invoke {@link CreateContainerCommand#exec()} to make a request.
 */
public class CreateContainerCommand extends Command<CreateContainerCommand, Container> {
    private final Config config = new Config();
    private NettyDockerClient nettyDockerClient;

    @Override
    public CreateContainerCommand withDockerClient(NettyDockerClient nettyDockerClient) {
        this.nettyDockerClient = nettyDockerClient;
        return this;
    }

    public CreateContainerCommand withHostConfig(HostConfig hostConfig) {
        config.setHostConfig(hostConfig);
        return this;
    }

    public CreateContainerCommand withNetworkingConfig(NetworkingConfig networkingConfig) {
        config.setNetworkingConfig(networkingConfig);
        return this;
    }

    public CreateContainerCommand withHostname(String hostname) {
        config.setHostName(hostname);
        return this;
    }

    public CreateContainerCommand withDomainName(String domainName) {
        config.setDomainName(domainName);
        return this;
    }

    public CreateContainerCommand withUser(String user) {
        config.setUser(user);
        return this;
    }

    public CreateContainerCommand withAttachStdin(boolean attachStdin) {
        config.setAttachStdin(attachStdin);
        return this;
    }

    public CreateContainerCommand withAttachStdout(boolean attachStdout) {
        config.setAttachStdout(attachStdout);
        return this;
    }

    public CreateContainerCommand withAttachStderr(boolean attachStderr) {
        config.setAttachStderr(attachStderr);
        return this;
    }

    public CreateContainerCommand withTty(boolean tty) {
        config.setTty(tty);
        return this;
    }

    public CreateContainerCommand withOpenStdin(boolean openStdin) {
        config.setOpenStdin(openStdin);
        return this;
    }

    public CreateContainerCommand withStdinOnce(boolean stdinOnce) {
        config.setStdinOnce(stdinOnce);
        return this;
    }

    public CreateContainerCommand withExposedPorts(ExposedPorts exposedPorts) {
        config.setExposedPorts(exposedPorts);
        return this;
    }

    public CreateContainerCommand withEnv(String[] env) {
        config.setEnv(env);
        return this;
    }

    public CreateContainerCommand withCmd(String[] cmd) {
        config.setCmd(cmd);
        return this;
    }

    public CreateContainerCommand withHealthConfig(HealthConfig healthConfig) {
        config.setHealthConfig(healthConfig);
        return this;
    }

    public CreateContainerCommand withArgsEscaped(boolean argsEscaped) {
        config.setArgsEscaped(argsEscaped);
        return this;
    }

    public CreateContainerCommand withImage(String image) {
        config.setImage(image);
        return this;
    }

    public CreateContainerCommand withVolumes(Volumes volumes) {
        config.setVolumes(volumes);
        return this;
    }

    public CreateContainerCommand withWorkingDir(String workingDir) {
        config.setWorkingDir(workingDir);
        return this;
    }

    public CreateContainerCommand withEntryPoint(String[] entryPoint) {
        config.setEntryPoint(entryPoint);
        return this;
    }

    public CreateContainerCommand withNetworkDisabled(boolean networkDisabled) {
        config.setNetworkDisabled(networkDisabled);
        return this;
    }

    public CreateContainerCommand withMacAddress(String macAddress) {
        config.setMacAddress(macAddress);
        return this;
    }

    public CreateContainerCommand withOnBuild(String[] onBuild) {
        config.setOnBuild(onBuild);
        return this;
    }

    public CreateContainerCommand withLabels(Map<String, String> labels) {
        config.setLabels(labels);
        return this;
    }

    public CreateContainerCommand withStopSignal(String stopSignal) {
        config.setStopSignal(stopSignal);
        return this;
    }

    public CreateContainerCommand withStopTimeout(int stopTimeout) {
        config.setStopTimeout(stopTimeout);
        return this;
    }

    public CreateContainerCommand withShell(String[] shell) {
        config.setShell(shell);
        return this;
    }

    @Override
    public Container exec() {
        try {
            String body = writer.writeValueAsString(config);
            URI uri = new URI(URIs.CREATE_CONTAINER.uri());
            ByteBuf bodyBuffer = Unpooled.copiedBuffer(body, CharsetUtil.UTF_8);
            FullHttpRequest req = RequestHelper.post(uri, true, bodyBuffer, HttpHeaderValues.APPLICATION_JSON);
            SimpleResponse simpleRes = nettyDockerClient.request(req).sync().get();
            String containerId = mapper.readTree(simpleRes.getBody()).get("Id").asText();
            return new Container().setContainerId(containerId).setConfig(config);

        } catch (JsonProcessingException | URISyntaxException e) {
            String errMsg = String.format("Exception raised while build the %s command", this.getClass().getSimpleName());
            throw new DockerRequestException(errMsg, e);

        } catch (InterruptedException | ExecutionException e) {
            String errMsg = String.format("Exception raised while build the %s command", this.getClass().getSimpleName());
            Thread.currentThread().interrupt();
            throw new DockerRequestException(errMsg, e);
        }
    }
}
