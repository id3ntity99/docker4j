package com.github.docker4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EndpointSettings {
    @JsonProperty("IPAMConfig")
    private IPAMConfig ipamConfig;
    @JsonProperty("Links")
    private String links;
    @JsonProperty("Aliases")
    private String[] aliases;
    @JsonProperty("NetworkID")
    private String networkId;
    @JsonProperty("EndpointID")
    private String endpointId;
    @JsonProperty("Gateway")
    private String gateway;
    @JsonProperty("IPAddress")
    private String ipAddress;
    @JsonProperty("IPPrefixLen")
    private int ipPrefixLen;
    @JsonProperty("IPv6Gateway")
    private String ipv6Gateway;
    @JsonProperty("GlobalIPv6Address")
    private String globalIPv6Address;
    @JsonProperty("GlobalIPv6PrefixLen")
    private int globalIPv6PrefixLen;
    @JsonProperty("MacAddress")
    private String macAddress;
    @JsonProperty("DriverOpts")
    private String driverOpts;

    public IPAMConfig getIpamConfig() {
        return ipamConfig;
    }

    public String getLinks() {
        return links;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getNetworkId() {
        return networkId;
    }

    public String getEndpointId() {
        return endpointId;
    }

    public String getGateway() {
        return gateway;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getIpPrefixLen() {
        return ipPrefixLen;
    }

    public String getIpv6Gateway() {
        return ipv6Gateway;
    }

    public String getGlobalIPv6Address() {
        return globalIPv6Address;
    }

    public int getGlobalIPv6PrefixLen() {
        return globalIPv6PrefixLen;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getDriverOpts() {
        return driverOpts;
    }
}