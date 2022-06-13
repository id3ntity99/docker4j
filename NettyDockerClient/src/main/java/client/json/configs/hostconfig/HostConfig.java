package client.json.configs.hostconfig;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

/**
 * Used for creation of container and container inspection.
 * This object will be converted to json object and sent to docker daemon.
 * Check /var/lib/docker/{container_id}/hostconfig.json
 */
public class HostConfig {
    @JsonProperty("CpuShares")
    private int cpuShares;
    @JsonProperty("Memory")
    private long memoryLimit;
    @JsonProperty("CgroupParent")
    private String cgroupParentPath;
    @JsonProperty("BlkioWeight")
    private int blockIoWeight;
    @JsonProperty("BlkioWeightDevice")
    private List<BlkIoWeightDevice> blkIoWeightDevices;
    @JsonProperty("BlkioDeviceReadBps")
    private List<BlkIoDeviceReadBps> blkIoDeviceReadBps;
    @JsonProperty("BlkioDeviceWriteBps")
    private List<BlkIoDeviceWriteBps> blkIoDeviceWriteBps;
    @JsonProperty("BlkioDeviceReadIOps")
    private List<BlkIoDeviceReadIOps> blkIoDeviceReadIOps;
    @JsonProperty("BlkioDeviceWriteIOps")
    private List<BlkIoDeviceWriteIOps> blkIoDeviceWriteIOps;
    @JsonProperty("CpuPeriod")
    private long cpuPeriod;
    @JsonProperty("CpuQuota")
    private long cpuQuota;
    @JsonProperty("CpuRealtimePeriod")
    private long cpuRealtimePeriod;
    @JsonProperty("CpuRealtimeRuntime")
    private long cpuRealtimeRuntime;
    @JsonProperty("CpusetCpus")
    private String cpuSetCpus;
    @JsonProperty("CpusetMems")
    private String cpuSetMems;
    @JsonProperty("Devices")
    private List<DeviceMapping> deviceMappings;
    @JsonProperty("DeviceCgroupRules")
    private String[] deviceCGroupRules;
    @JsonProperty("DeviceRequests")
    private List<DeviceRequest> deviceRequests;
    @JsonProperty("KernelMemoryTCP")
    private long kernelMemoryTCP;
    @JsonProperty("MemoryReservation")
    private long memoryReservation;
    @JsonProperty("MemorySwap")
    private long memorySwap;
    @JsonProperty("MemorySwappiness")
    private long memorySwappiness;
    @JsonProperty("NanoCpus")
    private long nanoCpus;
    @JsonProperty("OomKillDisable")
    private boolean oomKillDisable;
    @JsonProperty("Ulimits")
    private List<ULimit> uLimits;
    @JsonProperty("CpuCount")
    private long cpuCount;
    @JsonProperty("CpuPercent")
    private long cpuPercent;
    @JsonProperty("IOMaximumIOps")
    private long ioMaxIOPs;
    @JsonProperty("IOMaximumBandwidth")
    private long ioMaxBandwidth;
    @JsonProperty("Binds")
    private String[] binds;
    @JsonProperty("ContainerIDFile")
    private String containerIDFile;
    @JsonProperty("LogConfig")
    private LogConfig logConfig;
    @JsonProperty("NetworkMode")
    private String networkMode;
    @JsonProperty("PortBindings")
    private Map<String, List<PortBinding.HostPort>> portBindings;
    @JsonProperty("RestartPolicy")
    private RestartPolicy restartPolicy;

    public void setPortBindings(PortBinding portBinding) {
        this.portBindings = portBinding.getPortMap();
    }

    /**
     * Block IO weight (relative device weight).
     */
    public static class BlkIoWeightDevice {
        @JsonProperty("Path")
        private String path;
        @JsonProperty("Weight")
        private int weight;

        public BlkIoWeightDevice setPath(String path) {
            this.path = path;
            return this;
        }

        public BlkIoWeightDevice setWeight(int weight) {
            this.weight = weight;
            return this;
        }
    }

    /**
     * Limit read rate (bytes per second) from a device.
     */
    public static class BlkIoDeviceReadBps {
        @JsonProperty("Path")
        protected String path;
        @JsonProperty("Rate")
        protected long rate;

        public BlkIoDeviceReadBps setPath(String path) {
            this.path = path;
            return this;
        }

        public BlkIoDeviceReadBps setRate(long rate) {
            this.rate = rate;
            return this;
        }

        public String getPath() {
            return path;
        }

        public long getRate() {
            return rate;
        }
    }


    /**
     * Limit write rate (bytes per second) to a device.
     */
    public static class BlkIoDeviceWriteBps extends BlkIoDeviceReadBps {
    }

    /**
     * Limit read rate (IO per second) from a device.
     */
    public static class BlkIoDeviceReadIOps extends BlkIoDeviceReadBps {
    }

    /**
     * Limit write rate (IO per second) to a device.
     */
    public static class BlkIoDeviceWriteIOps extends BlkIoDeviceReadBps {

    }

    public static class DeviceMapping {
        @JsonProperty("PathOnHost")
        private String pathOnHost;
        @JsonProperty("PathInContainer")
        private String pathInContainer;
        @JsonProperty("CgroupPermissions")
        private String cGroupPermissions;

        public DeviceMapping setPathOnHost(String pathOnHost) {
            this.pathOnHost = pathOnHost;
            return this;
        }

        public DeviceMapping setPathInContainer(String pathInContainer) {
            this.pathInContainer = pathInContainer;
            return this;
        }

        public DeviceMapping setCGroupPermissions(String cGroupPermissions) {
            this.cGroupPermissions = cGroupPermissions;
            return this;
        }
    }

    public static class DeviceRequest {
        @JsonProperty("Driver")
        private String driver;
        @JsonProperty("Count")
        private int count;
        @JsonProperty("DeviceIDs")
        private String[] deviceIds;
        @JsonProperty("Capabilities")
        private String[] capabilities;
        @JsonProperty("Options")
        private Map<String, String> options;

        public DeviceRequest setDriver(String driver) {
            this.driver = driver;
            return this;
        }

        public DeviceRequest setCount(int count) {
            this.count = count;
            return this;
        }

        public DeviceRequest setDeviceIDs(String[] deviceIds) {
            this.deviceIds = deviceIds;
            return this;
        }

        public DeviceRequest setCapabilities(String[] capabilities) {
            this.capabilities = capabilities;
            return this;
        }

        public DeviceRequest setOptions(Map<String, String> options) {
            this.options = options;
            return this;
        }

        public String getDriver() {
            return driver;
        }

        public int getCount() {
            return count;
        }

        public String[] getDeviceIds() {
            return deviceIds;
        }

        public String[] getCapabilities() {
            return capabilities;
        }

        public Map<String, String> getOptions() {
            return options;
        }
    }

    public static class ULimit {
        @JsonProperty("Name")
        private String name;
        @JsonProperty("Soft")
        private int softLimit;
        @JsonProperty("Hard")
        private int hardLimit;

        public ULimit setName(String name) {
            this.name = name;
            return this;
        }

        public ULimit setSoftLimit(int softLimit) {
            this.softLimit = softLimit;
            return this;
        }

        public ULimit setHardLimit(int hardLimit) {
            this.hardLimit = hardLimit;
            return this;
        }

        public String getName() {
            return name;
        }

        public int getSoftLimit() {
            return softLimit;
        }

        public int getHardLimit() {
            return hardLimit;
        }
    }

    public static class LogConfig {
        @JsonProperty("Type")
        private String type;
        @JsonProperty("Config")
        private Map<String, String> config;

        public LogConfig setType(String type) {
            this.type = type;
            return this;
        }

        public LogConfig setConfig(Map<String, String> config) {
            this.config = config;
            return this;
        }

        public String getType() {
            return type;
        }

        public Map<String, String> getConfig() {
            return config;
        }
    }
}
