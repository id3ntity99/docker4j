package client.docker.model.exec;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExecStartConfig {
    @JsonProperty("Detach")
    private boolean detach;
    @JsonProperty("Tty")
    private boolean tty;

    public boolean isDetach() {
        return detach;
    }

    public ExecStartConfig setDetach(boolean detach) {
        this.detach = detach;
        return this;
    }

    public boolean isTty() {
        return tty;
    }

    public ExecStartConfig setTty(boolean tty) {
        this.tty = tty;
        return this;
    }
}
