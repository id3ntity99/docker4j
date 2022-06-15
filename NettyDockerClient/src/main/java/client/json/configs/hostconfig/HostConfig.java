package client.json.configs.hostconfig;

import client.json.configs.hostconfig.mount.Mount;
import client.json.configs.hostconfig.portbinding.PortBinding;
import client.json.configs.hostconfig.restartpolicy.RestartPolicy;
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
    private String cGroupParent;
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
    @JsonProperty("AutoRemove")
    private boolean autoRemove;
    @JsonProperty("VolumeDriver")
    private String volumeDriver;
    @JsonProperty("VolumesFrom")
    private String[] volumesFrom;
    @JsonProperty("Mounts")
    private List<Mount> mounts;
    @JsonProperty("CapAdd")
    private String[] capAdd;
    @JsonProperty("CapDrop")
    private String[] capDrop;
    @JsonProperty("CgroupnsMode")
    private String cGroupNsMode = "private";
    @JsonProperty("Dns")
    private String[] dns;
    @JsonProperty("DnsOptions")
    private String[] dnsOptions;
    @JsonProperty("DnsSearch")
    private String[] dnsSearch;
    @JsonProperty("ExtraHosts")
    private String[] extraHosts;
    @JsonProperty("GroupAdd")
    private String[] additionalGroup;
    @JsonProperty("IpcMode")
    private String ipcMode = "private";
    @JsonProperty("Cgroup")
    private String cGroup;
    @JsonProperty("Links")
    private String[] links;
    @JsonProperty("OomScoreAdj")
    private int oomScoreAdj;
    @JsonProperty("PidMode")
    private String pidMode;
    @JsonProperty("Privileged")
    private boolean privileged = false;
    @JsonProperty("ReadonlyRootfs")
    private boolean readOnlyRootFs = false;
    @JsonProperty("SecurityOpt")
    private String[] securityOpt;
    @JsonProperty("StorageOpt")
    private Map<String, String> storageOpt;
    @JsonProperty("Tmpfs")
    private Map<String, String> tmpfs;
    @JsonProperty("UTSMode")
    private String utsMode;
    @JsonProperty("UsernsMode")
    private String userNsMode;
    @JsonProperty("ShmSize")
    private String shmSize;
    @JsonProperty("Sysctls")
    private Map<String, String> sysctls;
    @JsonProperty("Runtime")
    private String runtime;
    @JsonProperty("ConsoleSize")
    private int[] consoleSize; // MS Windows only
    @JsonProperty("Isolation")
    private String isolation; // MS Windows only. Valid values = "default", "process", "hyperv"
    @JsonProperty("MaskedPaths")
    private String[] maskedPaths;
    @JsonProperty("ReadonlyPaths")
    private String[] readOnlyPaths;

    public int getCpuShares() {
        return cpuShares;
    }

    public HostConfig setCpuShares(int cpuShares) {
        this.cpuShares = cpuShares;
        return this;
    }

    public long getMemoryLimit() {
        return memoryLimit;
    }

    public HostConfig setMemoryLimit(long memoryLimit) {
        this.memoryLimit = memoryLimit;
        return this;
    }

    public String getCGroupParent() {
        return cGroupParent;
    }

    public HostConfig setCGroupParent(String cGroupParent) {
        this.cGroupParent = cGroupParent;
        return this;
    }

    public int getBlockIoWeight() {
        return blockIoWeight;
    }

    public HostConfig setBlockIoWeight(int blockIoWeight) {
        this.blockIoWeight = blockIoWeight;
        return this;
    }

    public List<BlkIoWeightDevice> getBlkIoWeightDevices() {
        return blkIoWeightDevices;
    }

    public HostConfig setBlkIoWeightDevices(List<BlkIoWeightDevice> blkIoWeightDevices) {
        this.blkIoWeightDevices = blkIoWeightDevices;
        return this;
    }

    public List<BlkIoDeviceReadBps> getBlkIoDeviceReadBps() {
        return blkIoDeviceReadBps;
    }

    public HostConfig setBlkIoDeviceReadBps(List<BlkIoDeviceReadBps> blkIoDeviceReadBps) {
        this.blkIoDeviceReadBps = blkIoDeviceReadBps;
        return this;
    }

    public List<BlkIoDeviceWriteBps> getBlkIoDeviceWriteBps() {
        return blkIoDeviceWriteBps;
    }

    public HostConfig setBlkIoDeviceWriteBps(List<BlkIoDeviceWriteBps> blkIoDeviceWriteBps) {
        this.blkIoDeviceWriteBps = blkIoDeviceWriteBps;
        return this;
    }

    public List<BlkIoDeviceReadIOps> getBlkIoDeviceReadIOps() {
        return blkIoDeviceReadIOps;
    }

    public HostConfig setBlkIoDeviceReadIOps(List<BlkIoDeviceReadIOps> blkIoDeviceReadIOps) {
        this.blkIoDeviceReadIOps = blkIoDeviceReadIOps;
        return this;
    }

    public List<BlkIoDeviceWriteIOps> getBlkIoDeviceWriteIOps() {
        return blkIoDeviceWriteIOps;
    }

    public HostConfig setBlkIoDeviceWriteIOps(List<BlkIoDeviceWriteIOps> blkIoDeviceWriteIOps) {
        this.blkIoDeviceWriteIOps = blkIoDeviceWriteIOps;
        return this;
    }

    public long getCpuPeriod() {
        return cpuPeriod;
    }

    public HostConfig setCpuPeriod(long cpuPeriod) {
        this.cpuPeriod = cpuPeriod;
        return this;
    }

    public long getCpuQuota() {
        return cpuQuota;
    }

    public HostConfig setCpuQuota(long cpuQuota) {
        this.cpuQuota = cpuQuota;
        return this;
    }

    public long getCpuRealtimePeriod() {
        return cpuRealtimePeriod;
    }

    public HostConfig setCpuRealtimePeriod(long cpuRealtimePeriod) {
        this.cpuRealtimePeriod = cpuRealtimePeriod;
        return this;
    }

    public long getCpuRealtimeRuntime() {
        return cpuRealtimeRuntime;
    }

    public HostConfig setCpuRealtimeRuntime(long cpuRealtimeRuntime) {
        this.cpuRealtimeRuntime = cpuRealtimeRuntime;
        return this;
    }

    public String getCpuSetCpus() {
        return cpuSetCpus;
    }

    public HostConfig setCpuSetCpus(String cpuSetCpus) {
        this.cpuSetCpus = cpuSetCpus;
        return this;
    }

    public String getCpuSetMems() {
        return cpuSetMems;
    }

    public HostConfig setCpuSetMems(String cpuSetMems) {
        this.cpuSetMems = cpuSetMems;
        return this;
    }

    public List<DeviceMapping> getDeviceMappings() {
        return deviceMappings;
    }

    public HostConfig setDeviceMappings(List<DeviceMapping> deviceMappings) {
        this.deviceMappings = deviceMappings;
        return this;
    }

    public String[] getDeviceCGroupRules() {
        return deviceCGroupRules;
    }

    public HostConfig setDeviceCGroupRules(String[] deviceCGroupRules) {
        this.deviceCGroupRules = deviceCGroupRules;
        return this;
    }

    public List<DeviceRequest> getDeviceRequests() {
        return deviceRequests;
    }

    public HostConfig setDeviceRequests(List<DeviceRequest> deviceRequests) {
        this.deviceRequests = deviceRequests;
        return this;
    }

    public long getKernelMemoryTCP() {
        return kernelMemoryTCP;
    }

    public HostConfig setKernelMemoryTCP(long kernelMemoryTCP) {
        this.kernelMemoryTCP = kernelMemoryTCP;
        return this;
    }

    public long getMemoryReservation() {
        return memoryReservation;
    }

    public HostConfig setMemoryReservation(long memoryReservation) {
        this.memoryReservation = memoryReservation;
        return this;
    }

    public long getMemorySwap() {
        return memorySwap;
    }

    public HostConfig setMemorySwap(long memorySwap) {
        this.memorySwap = memorySwap;
        return this;
    }

    public long getMemorySwappiness() {
        return memorySwappiness;
    }

    public HostConfig setMemorySwappiness(long memorySwappiness) {
        this.memorySwappiness = memorySwappiness;
        return this;
    }

    public long getNanoCpus() {
        return nanoCpus;
    }

    public HostConfig setNanoCpus(long nanoCpus) {
        this.nanoCpus = nanoCpus;
        return this;
    }

    public boolean isOomKillDisable() {
        return oomKillDisable;
    }

    public HostConfig setOomKillDisable(boolean oomKillDisable) {
        this.oomKillDisable = oomKillDisable;
        return this;
    }

    public List<ULimit> getULimits() {
        return uLimits;
    }

    public HostConfig setULimits(List<ULimit> uLimits) {
        this.uLimits = uLimits;
        return this;
    }

    public long getCpuCount() {
        return cpuCount;
    }

    public HostConfig setCpuCount(long cpuCount) {
        this.cpuCount = cpuCount;
        return this;
    }

    public long getCpuPercent() {
        return cpuPercent;
    }

    public HostConfig setCpuPercent(long cpuPercent) {
        this.cpuPercent = cpuPercent;
        return this;
    }

    public long getIoMaxIOPs() {
        return ioMaxIOPs;
    }

    public HostConfig setIoMaxIOPs(long ioMaxIOPs) {
        this.ioMaxIOPs = ioMaxIOPs;
        return this;
    }

    public long getIoMaxBandwidth() {
        return ioMaxBandwidth;
    }

    public HostConfig setIoMaxBandwidth(long ioMaxBandwidth) {
        this.ioMaxBandwidth = ioMaxBandwidth;
        return this;
    }

    public String[] getBinds() {
        return binds;
    }

    public HostConfig setBinds(String[] binds) {
        this.binds = binds;
        return this;
    }

    public String getContainerIDFile() {
        return containerIDFile;
    }

    public HostConfig setContainerIDFile(String containerIDFile) {
        this.containerIDFile = containerIDFile;
        return this;
    }

    public LogConfig getLogConfig() {
        return logConfig;
    }

    public HostConfig setLogConfig(LogConfig logConfig) {
        this.logConfig = logConfig;
        return this;
    }

    public String getNetworkMode() {
        return networkMode;
    }

    public HostConfig setNetworkMode(String networkMode) {
        this.networkMode = networkMode;
        return this;
    }

    public Map<String, List<PortBinding.HostPort>> getPortBindings() {
        return portBindings;
    }

    public HostConfig setPortBindings(Map<String, List<PortBinding.HostPort>> portBindings) {
        this.portBindings = portBindings;
        return this;
    }

    public RestartPolicy getRestartPolicy() {
        return restartPolicy;
    }

    public HostConfig setRestartPolicy(RestartPolicy restartPolicy) {
        this.restartPolicy = restartPolicy;
        return this;
    }

    public boolean isAutoRemove() {
        return autoRemove;
    }

    public HostConfig setAutoRemove(boolean autoRemove) {
        this.autoRemove = autoRemove;
        return this;
    }

    public String getVolumeDriver() {
        return volumeDriver;
    }

    public HostConfig setVolumeDriver(String volumeDriver) {
        this.volumeDriver = volumeDriver;
        return this;
    }

    public String[] getVolumesFrom() {
        return volumesFrom;
    }

    public HostConfig setVolumesFrom(String[] volumesFrom) {
        this.volumesFrom = volumesFrom;
        return this;
    }

    public List<Mount> getMounts() {
        return mounts;
    }

    public HostConfig setMounts(List<Mount> mounts) {
        this.mounts = mounts;
        return this;
    }

    public String[] getCapAdd() {
        return capAdd;
    }

    public HostConfig setCapAdd(String[] capAdd) {
        this.capAdd = capAdd;
        return this;
    }

    public String[] getCapDrop() {
        return capDrop;
    }

    public HostConfig setCapDrop(String[] capDrop) {
        this.capDrop = capDrop;
        return this;
    }

    public String getCGroupNsMode() {
        return cGroupNsMode;
    }

    public HostConfig setCGroupNsMode(String cGroupNsMode) {
        this.cGroupNsMode = cGroupNsMode;
        return this;
    }

    public String[] getDns() {
        return dns;
    }

    public HostConfig setDns(String[] dns) {
        this.dns = dns;
        return this;
    }

    public String[] getDnsOptions() {
        return dnsOptions;
    }

    public HostConfig setDnsOptions(String[] dnsOptions) {
        this.dnsOptions = dnsOptions;
        return this;
    }

    public String[] getDnsSearch() {
        return dnsSearch;
    }

    public HostConfig setDnsSearch(String[] dnsSearch) {
        this.dnsSearch = dnsSearch;
        return this;
    }

    public String[] getExtraHosts() {
        return extraHosts;
    }

    public HostConfig setExtraHosts(String[] extraHosts) {
        this.extraHosts = extraHosts;
        return this;
    }

    public String[] getAdditionalGroup() {
        return additionalGroup;
    }

    public HostConfig setAdditionalGroup(String[] additionalGroup) {
        this.additionalGroup = additionalGroup;
        return this;
    }

    public String getIpcMode() {
        return ipcMode;
    }

    public HostConfig setIpcMode(String ipcMode) {
        this.ipcMode = ipcMode;
        return this;
    }

    public String getCGroup() {
        return cGroup;
    }

    public HostConfig setCGroup(String cGroup) {
        this.cGroup = cGroup;
        return this;
    }

    public String[] getLinks() {
        return links;
    }

    public HostConfig setLinks(String[] links) {
        this.links = links;
        return this;
    }

    public int getOomScoreAdj() {
        return oomScoreAdj;
    }

    public HostConfig setOomScoreAdj(int oomScoreAdj) {
        this.oomScoreAdj = oomScoreAdj;
        return this;
    }

    public String getPidMode() {
        return pidMode;
    }

    public HostConfig setPidMode(String pidMode) {
        this.pidMode = pidMode;
        return this;
    }

    public boolean isPrivileged() {
        return privileged;
    }

    public HostConfig setPrivileged(boolean privileged) {
        this.privileged = privileged;
        return this;
    }

    public boolean isReadOnlyRootFs() {
        return readOnlyRootFs;
    }

    public HostConfig setReadOnlyRootFs(boolean readOnlyRootFs) {
        this.readOnlyRootFs = readOnlyRootFs;
        return this;
    }

    public String[] getSecurityOpt() {
        return securityOpt;
    }

    public HostConfig setSecurityOpt(String[] securityOpt) {
        this.securityOpt = securityOpt;
        return this;
    }

    public Map<String, String> getStorageOpt() {
        return storageOpt;
    }

    public HostConfig setStorageOpt(Map<String, String> storageOpt) {
        this.storageOpt = storageOpt;
        return this;
    }

    public Map<String, String> getTmpfs() {
        return tmpfs;
    }

    public HostConfig setTmpfs(Map<String, String> tmpfs) {
        this.tmpfs = tmpfs;
        return this;
    }

    public String getUtsMode() {
        return utsMode;
    }

    public HostConfig setUtsMode(String utsMode) {
        this.utsMode = utsMode;
        return this;
    }

    public String getUserNsMode() {
        return userNsMode;
    }

    public HostConfig setUserNsMode(String userNsMode) {
        this.userNsMode = userNsMode;
        return this;
    }

    public String getShmSize() {
        return shmSize;
    }

    public HostConfig setShmSize(String shmSize) {
        this.shmSize = shmSize;
        return this;
    }

    public Map<String, String> getSysctls() {
        return sysctls;
    }

    public HostConfig setSysctls(Map<String, String> sysctls) {
        this.sysctls = sysctls;
        return this;
    }

    public String getRuntime() {
        return runtime;
    }

    public HostConfig setRuntime(String runtime) {
        this.runtime = runtime;
        return this;
    }

    public int[] getConsoleSize() {
        return consoleSize;
    }

    public HostConfig setConsoleSize(int[] consoleSize) {
        this.consoleSize = consoleSize;
        return this;
    }

    public String getIsolation() {
        return isolation;
    }

    public HostConfig setIsolation(String isolation) {
        this.isolation = isolation;
        return this;
    }

    public String[] getMaskedPaths() {
        return maskedPaths;
    }

    public HostConfig setMaskedPaths(String[] maskedPaths) {
        this.maskedPaths = maskedPaths;
        return this;
    }

    public String[] getReadOnlyPaths() {
        return readOnlyPaths;
    }

    public HostConfig setReadOnlyPaths(String[] readOnlyPaths) {
        this.readOnlyPaths = readOnlyPaths;
        return this;
    }

    public HostConfig setPortBindings(PortBinding portBinding) {
        this.portBindings = portBinding.getPortMap();
        return this;
    }

    //TODO static class 분리

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
