package client.docker.commands;

import client.docker.commands.exceptions.DockerRequestException;
import client.docker.dockerclient.NettyDockerClient;
import client.docker.uris.URIs;
import io.netty.handler.codec.http.*;

import java.net.URI;
import java.net.URISyntaxException;

public class StartContainerCommand extends Command<StartContainerCommand, Void> {
    private String containerId;
    private NettyDockerClient nettyDockerClient;

    @Override
    public StartContainerCommand withDockerClient(NettyDockerClient nettyDockerClient) {
        this.nettyDockerClient = nettyDockerClient;
        return this;
    }

    public StartContainerCommand() {

    }

    public StartContainerCommand withContainerId(String containerId) {
        this.containerId = containerId;
        return this;
    }

    @Override
    public Void exec() {
        try {
            String stringUri = String.format(URIs.START_CONTAINER.uri(), containerId);
            URI uri = new URI(stringUri);
            FullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getRawPath());
            req.headers().set(HttpHeaderNames.HOST, uri.getHost());
            req.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            nettyDockerClient.request(req).sync();
        } catch (URISyntaxException e) {
            String errMsg = String.format("Exception raised while build the %s command", this.getClass().getSimpleName());
            throw new DockerRequestException(errMsg, e);
        } catch(InterruptedException e) {
            String errMsg = String.format("Exception raised while build the %s command", this.getClass().getSimpleName());
            Thread.currentThread().interrupt();
            throw new DockerRequestException(errMsg, e);
        }
        return null;
    }
}
