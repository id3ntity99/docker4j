package client.docker.commands;

import client.docker.commands.exceptions.CommandBuildException;
import client.docker.uris.URIs;
import io.netty.handler.codec.http.*;

import java.net.URI;
import java.net.URISyntaxException;

public class StartContainerCommand extends Command {
    private final String containerId;

    public StartContainerCommand(String containerId) {
        this.containerId = containerId;
    }

    @Override
    public FullHttpRequest build() {
        try {
            String stringUri = String.format(URIs.START_CONTAINER.uri(), containerId);
            URI uri = new URI(stringUri);
            FullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getRawPath());
            req.headers().set(HttpHeaderNames.HOST, uri.getHost());
            req.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            return req;
        } catch (URISyntaxException e) {
            String errMsg = String.format("Exception raised while build the %s command", this.getClass().getSimpleName());
            throw new CommandBuildException(errMsg, e);
        }
    }
}
