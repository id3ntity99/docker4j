package com.github.docker4j;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.docker4j.model.*;

public class InspectResponse implements DockerResponse {
    @JsonProperty("Id")
    private String containerId;
    @JsonProperty("Created")
    private String created;
    @JsonProperty("Path")
    private String path;
    @JsonProperty("Args")
    private String[] args;
    @JsonProperty("State")
    private ContainerState state;
    @JsonProperty("Image")
    private String image;
    @JsonProperty("ResolvConfPath")
    private String resolveConfPath;
    @JsonProperty("HostnamePath")
    private String hostNamePath;
    @JsonProperty("HostsPath")
    private String hostsPath;
    @JsonProperty("LogPath")
    private String logPath;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("RestartCount")
    private int restartCount;
    @JsonProperty("Driver")
    private String driver;
    @JsonProperty("Platform")
    private String platform;
    @JsonProperty("MountLabel")
    private String mountLabel;
    @JsonProperty("ProcessLabel")
    private String processLabel;
    @JsonProperty("AppArmorProfile")
    private String appArmorProfile;
    @JsonProperty("ExecIDs")
    private String[] execIds;
    @JsonProperty("HostConfig")
    private HostConfig hostConfig;
    @JsonProperty("GraphDriver")
    private GraphDriver graphDriver;
    @JsonProperty("SizeRw")
    private long sizeRw;
    @JsonProperty("SizeRootFs")
    private long sizeRootFs;
    @JsonProperty("Mounts")
    private MountPoint[] mountPoints;
    @JsonProperty("Config")
    private Config config;
    @JsonProperty("NetworkSettings")
    private NetworkSettings networkSettings;

    @Override
    public String getContainerId() {
        return containerId;
    }

    @Override
    public String[] getExecIds() {
        return execIds;
    }

    public String getCreated() {
        return created;
    }

    public String getPath() {
        return path;
    }

    public String[] getArgs() {
        return args;
    }

    public ContainerState getState() {
        return state;
    }

    public String getImage() {
        return image;
    }

    public String getResolveConfPath() {
        return resolveConfPath;
    }

    public String getHostNamePath() {
        return hostNamePath;
    }

    public String getHostsPath() {
        return hostsPath;
    }

    public String getLogPath() {
        return logPath;
    }

    public String getName() {
        return name;
    }

    public int getRestartCount() {
        return restartCount;
    }

    public String getDriver() {
        return driver;
    }

    public String getPlatform() {
        return platform;
    }

    public String getMountLabel() {
        return mountLabel;
    }

    public String getProcessLabel() {
        return processLabel;
    }

    public String getAppArmorProfile() {
        return appArmorProfile;
    }

    public HostConfig getHostConfig() {
        return hostConfig;
    }

    public GraphDriver getGraphDriver() {
        return graphDriver;
    }

    public long getSizeRw() {
        return sizeRw;
    }

    public long getSizeRootFs() {
        return sizeRootFs;
    }

    public MountPoint[] getMountPoints() {
        return mountPoints;
    }

    public Config getConfig() {
        return config;
    }

    public NetworkSettings getNetworkSettings() {
        return networkSettings;
    }
}