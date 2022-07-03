package client.docker.commands;

import com.fasterxml.jackson.annotation.JsonProperty;

@Deprecated
public class ExecCreateResponse {
    @JsonProperty("Id")
    private String execId;

    public String getExecId() {
        return execId;
    }

    public void setExecId(String execId) {
        this.execId = execId;
    }
}
