package client.docker.util;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;

import java.net.URI;

public class RequestHelper {
    public static FullHttpRequest post(URI uri, boolean withEncoding,
                                       ByteBuf body, AsciiString contentType) {
        FullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getRawPath());
        req.headers().set(HttpHeaderNames.HOST, uri.getHost());
        req.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        if (withEncoding) {
            req.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
            req.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
        }
        if (body != null) {
            req.content().writeBytes(body);
            req.headers().set(HttpHeaderNames.CONTENT_LENGTH, body.readableBytes());
        }
        if (contentType != null) {
            req.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
        }
        return req;
    }
}
