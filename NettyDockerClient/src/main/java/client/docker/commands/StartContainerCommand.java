package client.docker.commands;

import client.docker.dockerclient.NettyDockerClient;
import client.docker.request.exceptions.DockerRequestException;
import client.docker.uris.URIs;
import client.docker.util.RequestHelper;
import io.netty.handler.codec.http.FullHttpRequest;

import java.net.URI;

/**
 * @deprecated Use {@link client.docker.request.DockerRequest} instead.
 */
public class StartContainerCommand extends Command<StartContainerCommand, Void> {
    private String containerId;
    private NettyDockerClient nettyDockerClient;

    @Override
    public StartContainerCommand withDockerClient(NettyDockerClient nettyDockerClient) {
        this.nettyDockerClient = nettyDockerClient;
        return this;
    }

    public StartContainerCommand withContainerId(String containerId) {
        this.containerId = containerId;
        return this;
    }

    @Override
    public Void exec() {
        try {
            URI uri = URIs.START_CONTAINER.uri(containerId);
            FullHttpRequest req = RequestHelper.post(uri, false, null, null);
            nettyDockerClient.request(req).sync();
        } catch (InterruptedException e) {
            String errMsg = String.format("Exception raised while build the %s command", this.getClass().getSimpleName());
            Thread.currentThread().interrupt();
            throw new DockerRequestException(errMsg, e);
        }
        return null;
    }
}
