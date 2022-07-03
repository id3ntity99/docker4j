package client.docker.commands;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.netty.handler.codec.http.FullHttpRequest;

import java.net.URI;

/**
 * @deprecated
 * This class is used to contain HTTP response body.
 * You can use this object to map JSON.
 * The fields are extracted from {@link client.nettyserver.SimpleResponse#getBody()}.
 * Thus, the overall flow is: <br/>
 * <br/>
 * {@link client.nettyserver.HttpClient#request(URI, FullHttpRequest)} returns SimpleResponse <br/>
 * -> {@link CreateContainerCommand#exec()} checks if the container is created normally and <br/>
 * -> map the {@link client.nettyserver.SimpleResponse#getBody()} (which returns JSON string) to {@link CreateContainerResponse}
 */
@Deprecated
public class CreateContainerResponse {
    @JsonProperty("Id")
    private String id;
    @JsonProperty("Warnings")
    private String[] warnings;

    public String getId() {
        return id;
    }

    public String[] getWarnings() {
        return warnings;
    }
}
