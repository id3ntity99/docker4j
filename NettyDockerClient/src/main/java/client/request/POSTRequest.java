package client.request;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;

import java.net.URI;

@Deprecated
public class POSTRequest {
    private boolean isKeepAlive = false;
    private URI uri;
    private ByteBuf body;
    private AsciiString acceptEncoding = HttpHeaderValues.GZIP;
    private HttpVersion version = HttpVersion.HTTP_1_1;

    private AsciiString contentType = HttpHeaderValues.APPLICATION_JSON;

    public POSTRequest withBody(String body) {
        this.body = Unpooled.copiedBuffer(body, CharsetUtil.UTF_8);
        return this;
    }

    public POSTRequest withKeepAlive(boolean isKeepalive) {
        this.isKeepAlive = isKeepalive;
        return this;
    }

    public POSTRequest withURI(URI uri) {
        this.uri = uri;
        return this;
    }

    public POSTRequest withHttpVersion(HttpVersion version) {
        this.version = version;
        return this;
    }

    public POSTRequest withAcceptEncoding(AsciiString acceptEncoding) {
        this.acceptEncoding = acceptEncoding;
        return this;
    }

    public FullHttpRequest build() {
        FullHttpRequest req = new DefaultFullHttpRequest(version, HttpMethod.POST, uri.getRawPath());
        req.headers().set(HttpHeaderNames.HOST, uri.getHost());
        if (isKeepAlive) {
            req.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        } else {
            req.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        }
        req.headers().set(HttpHeaderNames.ACCEPT_ENCODING, acceptEncoding);
        req.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
        if (body != null) {
            req.headers().add(HttpHeaderNames.CONTENT_TYPE, contentType);
            req.content().writeBytes(body);
            req.headers().set(HttpHeaderNames.CONTENT_LENGTH, body.readableBytes());
        }
        req.headers().set(HttpHeaderNames.ACCEPT, "*/*");
        return req;
    }
}
