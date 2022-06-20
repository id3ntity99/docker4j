package client.nettyclient.response;

import client.nettyclient.HttpClient;
import io.netty.handler.codec.http.FullHttpRequest;

import java.net.URI;

/**
 * A simple response class that aggregates HTTP status code and response body.
 * This class is used to return HTTP response to the caller of {@link HttpClient#request(URI, FullHttpRequest)}
 */
public class SimpleResponse {
    private int statusCode;
    private String body;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
