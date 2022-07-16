package com.github.docker4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "ipamconfig")
@JsonSubTypes(
        @JsonSubTypes.Type(value = IPAMConfig.class, name = "IPAMConfig")
)
public class EndpointsConfig {
    @JsonProperty("IPAMConfig")
    private IPAMConfig ipamConfig = new IPAMConfig();
    @JsonProperty("Links")
    private String[] links;
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
    private String globalIpv6Address;
    @JsonProperty("GlobalIPv6PrefixLen")
    private long globalIpv6PrefixLen;
    @JsonProperty("MACAddress")
    private String macAddress;
    @JsonProperty("DriverOpts")
    private Map<String, String> driverOpts;

    @JsonProperty("IPAMConfig")
    public EndpointsConfig setIPAMConfig(IPAMConfig ipamConfig) {
        this.ipamConfig = ipamConfig;
        return this;
    }

    public EndpointsConfig setLinks(String[] links) {
        this.links = links;
        return this;
    }

    public EndpointsConfig setAliases(String[] aliases) {
        this.aliases = aliases;
        return this;
    }

    public EndpointsConfig setNetworkId(String networkId) {
        this.networkId = networkId;
        return this;
    }

    public EndpointsConfig setEndpointId(String endpointId) {
        this.endpointId = endpointId;
        return this;
    }

    public EndpointsConfig setGateway(String gateway) {
        this.gateway = gateway;
        return this;
    }

    public EndpointsConfig setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public EndpointsConfig setIpPrefixLen(int ipPrefixLen) {
        this.ipPrefixLen = ipPrefixLen;
        return this;
    }

    public EndpointsConfig setIpv6Gateway(String ipv6Gateway) {
        this.ipv6Gateway = ipv6Gateway;
        return this;
    }

    public EndpointsConfig setGlobalIpv6Address(String gIPv6Address) {
        this.globalIpv6Address = gIPv6Address;
        return this;
    }

    public EndpointsConfig setGlobalIpv6PrefixLen(long globalIPv6PrefixLen) {
        this.globalIpv6PrefixLen = globalIPv6PrefixLen;
        return this;
    }

    public EndpointsConfig setMacAddress(String macAddress) {
        this.macAddress = macAddress;
        return this;
    }

    public EndpointsConfig setDriverOpts(Map<String, String> driverOpts) {
        this.driverOpts = driverOpts;
        return this;
    }

    public IPAMConfig getIPAMConfig() {
        return ipamConfig;
    }

    public String[] getLinks() {
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

    public String getGlobalIpv6Address() {
        return globalIpv6Address;
    }

    public long getGlobalIpv6PrefixLen() {
        return globalIpv6PrefixLen;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public Map<String, String> getDriverOpts() {
        return driverOpts;
    }
}
