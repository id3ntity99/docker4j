package client.nettyclient;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * A simple response class that aggregates HTTP status code and response body.
 * This class is used to return HTTP response to the caller of {@link HttpClient#request(FullHttpRequest)}
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
