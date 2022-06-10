package client;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import request.Request;

import java.net.URI;

public class ClientRunner {
    private static final HttpClient CLIENT = new HttpClient();

    public static void main(String[] args) throws Exception {
        URI uri = new URI("http://localhost:2375/version");
        FullHttpRequest request = new Request().withMethod(HttpMethod.GET)
                .withURI(uri)
                .withKeepAlive(false)
                .withVersion(HttpVersion.HTTP_1_1)
                .build();
        CLIENT.connect(uri);
        CLIENT.request(request);
    }
}
