package request;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;

public class Request {
    private HttpMethod method;
    private JSONObject body;
    private boolean isKeepAlive;
    private URI uri;
    private HttpVersion version;
    private String contentType;

    private FullHttpRequest createBaseRequest() {
        FullHttpRequest req = new DefaultFullHttpRequest(version, method, uri.getRawPath());
        req.headers().set(HttpHeaderNames.HOST, uri.getHost());
        if (isKeepAlive) {
            req.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        } else {
            req.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        }
        req.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
        return req;
    }

    private FullHttpRequest getRequest() {
        return createBaseRequest();
    }

    private FullHttpRequest postRequest() {
        FullHttpRequest req = createBaseRequest();
        req.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
        ByteBuf jsonBuffer = Unpooled.copiedBuffer(body.toString().getBytes(CharsetUtil.UTF_8));
        req.content().clear().writeBytes(jsonBuffer);
        req.headers().set(HttpHeaderNames.CONTENT_LENGTH, jsonBuffer.readableBytes());
        return req;
    }

    public Request withMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    public Request withContentType(String contentType) throws IOException{
        if (method != HttpMethod.POST) {
            throw new IOException("Expected HttpMethod.POST, received: " + method);
        }
        this.contentType = contentType;
        return this;
    }

    public Request withBody(JSONObject body) throws IOException {
        if (method != HttpMethod.POST) {
            throw new IOException("Expected HttpMethod.POST, received: " + method);
        }
        this.body = body;
        return this;
    }

    public Request withKeepAlive(boolean isKeepAlive) {
        this.isKeepAlive = isKeepAlive;
        return this;
    }

    public Request withURI(URI uri) {
        this.uri = uri;
        return this;
    }

    public Request withVersion(HttpVersion version) {
        this.version = version;
        return this;
    }

    public FullHttpRequest build() {
        FullHttpRequest req = null;
        if (method == HttpMethod.GET) {
            req = getRequest();
        } else if (method == HttpMethod.POST) {
            req = postRequest();
        }
        return req;
    }

}
