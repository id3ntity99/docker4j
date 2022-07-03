package client.docker.model;

import client.docker.configs.config.Config;

public class Container {
    private Config config;
    private String containerId;
    private String execId;

    public Config getConfig() {
        return config;
    }

    public Container setConfig(Config config) {
        this.config = config;
        return this;
    }

    public String getContainerId() {
        return containerId;
    }

    public Container setContainerId(String containerId) {
        this.containerId = containerId;
        return this;
    }

    public String getExecId() {
        return execId;
    }

    public Container setExecId(String execId) {
        this.execId = execId;
        return this;
    }
}
