package client.docker.configs.hostconfig.networkingconfig;

import client.docker.configs.hostconfig.networkingconfig.endpointsconfig.EndpointsConfig;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class NetworkingConfig {
    @JsonProperty("EndpointsConfig")
    private Map<String, EndpointsConfig> endpointsConfig;

    public NetworkingConfig setEndpointsConfig(Map<String, EndpointsConfig> endpointsConfig) {
        this.endpointsConfig = endpointsConfig;
        return this;
    }
}
