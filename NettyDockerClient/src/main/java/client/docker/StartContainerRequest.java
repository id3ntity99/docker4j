package client.docker;

import client.docker.internal.http.RequestHelper;
import client.docker.internal.http.URIs;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

import java.net.URI;

public class StartContainerRequest extends DockerRequest {
    public StartContainerRequest(Builder builder) {
        super(builder);
    }

    @Override
    public FullHttpRequest render() {
        String id = node.find("_container_id");
        URI uri = URIs.START_CONTAINER.uri(id);
        logger.debug("Rendered FullHttpRequest. URL == {}", uri);
        return RequestHelper.post(uri, false, null, null);
    }

    @Override
    protected ChannelInboundHandlerAdapter handler() {
        return new StartContainerHandler().setNextRequest(nextRequest)
                .setAllocator(allocator)
                .setPromise(promise)
                .setNode(node);
    }

    public static class Builder implements DockerRequestBuilder {
        @Override
        public DockerRequest build() {
            return new StartContainerRequest(this);
        }
    }
}
