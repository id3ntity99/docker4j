package client.docker.commands;

import client.nettyclient.response.SimpleResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.netty.handler.codec.http.FullHttpRequest;

import java.net.URI;

/**
 * This class is used to contain HTTP response body.
 * You can use this object to map JSON.
 * The fields are extracted from {@link SimpleResponse#getBody()}.
 * Thus, the overall flow is: <br/>
 * <br/>
 * {@link client.nettyclient.HttpClient#request(URI, FullHttpRequest)} returns SimpleResponse <br/>
 * -> {@link CreateContainerCmd#exec()} checks if the container is created normally and <br/>
 * -> map the {@link SimpleResponse#getBody()} (which returns JSON string) to {@link CreateContainerResponse}
 */
public class CreateContainerResponse {
    @JsonProperty("Id")
    private String id;
    @JsonProperty("Warnings")
    private String[] warnings;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String[] getWarnings() {
        return warnings;
    }

    public void setWarnings(String[] warnings) {
        this.warnings = warnings;
    }
}
