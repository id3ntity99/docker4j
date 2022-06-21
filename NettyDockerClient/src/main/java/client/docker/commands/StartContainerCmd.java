package client.docker.commands;

import client.docker.uris.URIs;
import client.nettyclient.HttpClient;
import client.nettyclient.response.SimpleResponse;
import io.netty.handler.codec.http.*;

import java.net.URI;

public class StartContainerCmd extends AbstractCommand<Void> {
    private final HttpClient httpClient = new HttpClient();
    private final String containerId;

    public StartContainerCmd(String containerId) {
        this.containerId = containerId;
    }

    @Override
    public Void exec() throws Exception {
        String stringUri = String.format(URIs.START_CONTAINER.uri(), containerId);
        URI uri = new URI(stringUri);
        FullHttpRequest req = StartContainerRequest.create(uri);
        SimpleResponse simpleRes = httpClient.request(uri, req);
        if (simpleRes.getStatusCode() != 204) {
            throw new Exception("Cannot start the container due to: " + simpleRes.getBody());
        }
        return null;
    }

    private static class StartContainerRequest {
        public static FullHttpRequest create(URI uri) {
            FullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getRawPath());
            req.headers().set(HttpHeaderNames.HOST, uri.getHost());
            req.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            return req;
        }
    }
}
