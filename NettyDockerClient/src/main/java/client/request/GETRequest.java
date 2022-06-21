package client.request;

import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;

import java.net.URI;

@Deprecated
public class GETRequest {
    private boolean isKeepAlive = false;
    private URI uri;
    private HttpVersion version = HttpVersion.HTTP_1_1;
    private AsciiString acceptEncoding = HttpHeaderValues.GZIP;

    public GETRequest withKeepAlive(boolean isKeepAlive) {
        this.isKeepAlive = isKeepAlive;
        return this;
    }

    public GETRequest withURI(URI uri) {
        this.uri = uri;
        return this;
    }

    public GETRequest withAcceptEncoding(AsciiString acceptEncoding) {
        this.acceptEncoding = acceptEncoding;
        return this;
    }

    public GETRequest withHttpVersion(HttpVersion version) {
        this.version = version;
        return this;
    }

    public FullHttpRequest build() {
        FullHttpRequest req = new DefaultFullHttpRequest(version, HttpMethod.GET, uri.getRawPath());
        req.headers().set(HttpHeaderNames.HOST, uri.getHost());
        if (isKeepAlive) {
            req.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        } else {
            req.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        }
        req.headers().set(HttpHeaderNames.ACCEPT_ENCODING, acceptEncoding);
        req.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
        return req;
    }
}