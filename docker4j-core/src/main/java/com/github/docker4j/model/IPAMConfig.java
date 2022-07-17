package docker4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IPAMConfig {
    @JsonProperty("IPv4Address")
    private String ipv4Address;
    @JsonProperty("IPv6Address")
    private String ipv6Address;
    @JsonProperty("LinkLocalIPs")
    private String[] linkLocalIps;

    public IPAMConfig setIPv4Address(String ipv4Address) {
        this.ipv4Address = ipv4Address;
        return this;
    }

    public IPAMConfig setIPv6Address(String ipv6Address) {
        this.ipv6Address = ipv6Address;
        return this;
    }

    public IPAMConfig setLinkLocalIps(String[] linkLocalIps) {
        this.linkLocalIps = linkLocalIps;
        return this;
    }

    public String getIpv4Address() {
        return ipv4Address;
    }

    public String getIpv6Address() {
        return ipv6Address;
    }

    public String[] getLinkLocalIps() {
        return linkLocalIps;
    }
}
