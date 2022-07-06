package client.docker.request;

import client.docker.uris.URIs;
import client.docker.util.RequestHelper;
import io.netty.handler.codec.http.FullHttpRequest;

import java.net.URI;

public class StartContainerRequest extends DockerRequest {
    public StartContainerRequest(Builder builder) {
        super(builder);
    }

    @Override
    public FullHttpRequest render() {
        String containerId = container.getContainerId();
        URI uri = URIs.START_CONTAINER.uri(containerId);
        logger.debug(String.format("Rendered FullHttpRequest. URL == %s", uri));
        return RequestHelper.post(uri, false, null, null);
    }

    @Override
    public DockerResponseHandler handler() {
        return new StartContainerHandler(container, nextRequest, promise);
    }

    public static class Builder implements DockerRequestBuilder {
        @Override
        public DockerRequest build() {
            return new StartContainerRequest(this);
        }
    }
}
