package com.github.docker4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class NetworkSettings {
    @JsonProperty("Bridge")
    private String bridge;
    @JsonProperty("SandboxID")
    private String sandboxId;
    @JsonProperty("HairpinMode")
    private boolean hairpinMode;
    @JsonProperty("LinkLocalIPv6Address")
    private String linkLocalIPv6Address;
    @JsonProperty("LinkLocalIPv6PrefixLen")
    private String linkLocalIPv6PrefixLen;
    @JsonProperty("Ports")
    private Map<String, PortBinding> ports;
    @JsonProperty("SandboxKey")
    private String sandboxKey;
    @JsonProperty("SecondaryIPAddresses")
    private String[] secondaryIPAddresses;
    @JsonProperty("SecondaryIPv6Addresses")
    private String[] secondaryIPv6Addresses;
    @JsonProperty("EndpointID")
    private String endpointId;
    @JsonProperty("Gateway")
    private String gateway;
    @JsonProperty("GlobalIPv6Address")
    private String globalIPv6Address;
    @JsonProperty("GlobalIPv6PrefixLen")
    private int globalIPv6PrefixLen;
    @JsonProperty("IPAddress")
    private String ipAddress;
    @JsonProperty("IPPrefixLen")
    private int ipPrefixLen;
    @JsonProperty("IPv6Gateway")
    private String ipv6Gateway;
    @JsonProperty("MacAddress")
    private String macAddress;
    @JsonProperty("Networks")
    private Map<String, EndpointSettings> networks;

    public String getBridge() {
        return bridge;
    }

    public String getSandboxId() {
        return sandboxId;
    }

    public boolean isHairpinMode() {
        return hairpinMode;
    }

    public String getLinkLocalIPv6Address() {
        return linkLocalIPv6Address;
    }

    public String getLinkLocalIPv6PrefixLen() {
        return linkLocalIPv6PrefixLen;
    }

    public Map<String, PortBinding> getPorts() {
        return ports;
    }

    public String getSandboxKey() {
        return sandboxKey;
    }

    public String[] getSecondaryIPAddresses() {
        return secondaryIPAddresses;
    }

    public String[] getSecondaryIPv6Addresses() {
        return secondaryIPv6Addresses;
    }

    public String getEndpointId() {
        return endpointId;
    }

    public String getGateway() {
        return gateway;
    }

    public String getGlobalIPv6Address() {
        return globalIPv6Address;
    }

    public int getGlobalIPv6PrefixLen() {
        return globalIPv6PrefixLen;
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

    public String getMacAddress() {
        return macAddress;
    }

    public Map<String, EndpointSettings> getNetworks() {
        return networks;
    }
}
