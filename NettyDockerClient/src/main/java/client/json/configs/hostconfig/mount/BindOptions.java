package client.json.configs.hostconfig.mount;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BindOptions {
    @JsonProperty("Propagation")
    private String propagation;
    @JsonProperty("NonRecursive")
    private boolean nonRecursive = false;

    public BindOptions setPropagation(String propagation) {
        this.propagation = propagation;
        return this;
    }

    public BindOptions setNonRecursive(boolean nonRecursive) {
        this.nonRecursive = nonRecursive;
        return this;
    }
}
