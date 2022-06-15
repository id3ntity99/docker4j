package client.json.configs.config;

import client.json.configs.hostconfig.HostConfig;
import client.json.configs.hostconfig.networkingconfig.NetworkingConfig;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * An instance of this class will be used for mapping to a JSON string.
 * Then, the JSON string is used to request create container and inspect container.
 * This class aggregates  {@link HostConfig} and {@link NetworkingConfig} objects using {@link Config#setHostConfig(HostConfig)}
 * and {@link Config#setNetworkingConfig(NetworkingConfig)}.
 */
public class Config {
    @JsonProperty("Hostname")
    private String hostName = "";
    @JsonProperty("Domainname")
    private String domainName = "";
    @JsonProperty("User")
    private String user = "";
    @JsonProperty("AttachStdin")
    private boolean attachStdin = false;
    @JsonProperty("AttachStdout")
    private boolean attachStdout = true;
    @JsonProperty("AttachStderr")
    private boolean attachStderr = true;
    @JsonProperty("ExposedPorts")
    private Map<String, Object> exposedPorts;
    @JsonProperty("Tty")
    private boolean tty = false;
    @JsonProperty("OpenStdin")
    private boolean openStdin = false;
    @JsonProperty("StdinOnce")
    private boolean stdinOnce = false;
    @JsonProperty("Env")
    private String[] env;
    @JsonProperty("Cmd")
    private String[] cmd = {};
    @JsonProperty("Healthcheck")
    private HealthConfig healthConfig;
    @JsonProperty("ArgsEscaped")
    private boolean argsEscaped = false;
    @JsonProperty("Image")
    private String image = "";
    @JsonProperty("Volumes")
    private Map<String, String> volumes;
    @JsonProperty("WorkingDir")
    private String workingDir = "";
    @JsonProperty("Entrypoint")
    private String[] entryPoint = null;
    @JsonProperty("NetworkDisabled")
    private boolean networkDisabled;
    @JsonProperty("MacAddress")
    private String macAddress;
    @JsonProperty("OnBuild")
    private String[] onBuild;
    @JsonProperty("Labels")
    private Map<String, String> labels = null;
    @JsonProperty("StopSignal")
    private String stopSignal;
    @JsonProperty("StopTimeout")
    private int stopTimeout;
    @JsonProperty("Shell")
    private String[] shell;
    @JsonProperty("HostConfig")
    private HostConfig hostConfig = new HostConfig();
    @JsonProperty("NetworkingConfig")
    private NetworkingConfig networkingConfig = new NetworkingConfig();

    public String getHostName() {
        return hostName;
    }

    /**
     * Sets hostname of the container to create.
     * If hostname is not specified, Docker uses the container id as a hostname.
     *
     * @param hostName A hostname for container's UTS namespace. Valid hostname is specified at RFC 1123.
     * @return Config object with initialized <code>hostName</code> field.
     */
    public Config setHostName(String hostName) {
        this.hostName = hostName;
        return this;
    }

    public String getDomainName() {
        return domainName;
    }

    /**
     * Sets domain name used for the container.
     *
     * @param domainName A NIS domain name for container's UTS namespace.
     * @return Config object with initialized <code>domainName</code> field.
     * @see <a href="https://www.ibm.com/docs/en/aix/7.2?topic=nis-network-information-service">Network Information Service(NIS)</a>,
     * <a href="https://www.bell-labs.com/usr/dmr/www/retro.pdf">UNIX Time-Sharing System(UTS)</a>,
     * <a href="https://lwn.net/Articles/179345/">UTS namespaces</a>,
     * <a href="https://lwn.net/Articles/531114/">Namespaces</a>,
     */
    public Config setDomainName(String domainName) {
        this.domainName = domainName;
        return this;
    }

    public String getUser() {
        return user;
    }

    /**
     * Sets the user of a container to create.
     *
     * @param user A system user of the container.
     * @return Config object with initialized <code>user</code> field.
     */
    public Config setUser(String user) {
        this.user = user;
        return this;
    }

    public boolean isAttachStdin() {
        return attachStdin;
    }

    /**
     * Whether attach a stdin named-pipe to a container or not.
     * A stdin named-pipe resides in <strong>/run/docker/containerd/{container_id}</strong> if you are using Linux and the container.
     * The named pipe is generated when the container has been already started.
     *
     * @param attachStdin Set this parameter to <strong>true</strong> to attach a stdin named-pipe to a container.
     *                    Default: <strong>false</strong>
     * @return Config object with initialized <code>isAttachStdin</code> field.
     */
    public Config setAttachStdin(boolean attachStdin) {
        this.attachStdin = attachStdin;
        return this;
    }

    public boolean isAttachStdout() {
        return attachStdout;
    }

    /**
     * Whether attach a stdout named-pipe to a container or not. A stdout named-pipe resides in
     * <strong>/run/docker/containerd/{container_id}</strong> if you are using Linux.
     * The named pipe is generated when the container has been already started.
     *
     * @param attachStdout Set this parameter to <strong>false</strong> if you don't need stdout.
     *                     Default: <strong>true</strong>
     * @retur Config object with initialized isAttachStdout field.
     */
    public Config setAttachStdout(boolean attachStdout) {
        this.attachStdout = attachStdout;
        return this;
    }

    public boolean isAttachStderr() {
        return attachStderr;
    }

    /**
     * Whether attach a stderr named-pipe to a container or not. A stderr named-pipe resides in
     * <strong>/run/docker/containerd/{container_id}</strong> if you are using Linux.
     * Note that, if you set the parameter of {@link Config#setTty(boolean)} to <code>true</code>,
     * then stderr named-pipe won't be generated in the path mentioned earlier, even if {@link Config#isAttachStderr()} is set to <strong>true</strong>.
     * The named pipe is generated when the container has been already started.
     *
     * @param attachStderr Set this parameter to <strong>false</strong> if a stderr is not need. Default: <strong>true</strong>
     * @return <code>Config</code> object with initialized <code>isAttachStderr</code> field.
     */
    public Config setAttachStderr(boolean attachStderr) {
        this.attachStderr = attachStderr;
        return this;
    }

    public Map<String, Object> getExposedPorts() {
        return exposedPorts;
    }

    /**
     * Sets a <code>Map</code> to specify the exposed ports.
     *
     * @param exposedPorts Key must satisfy the form "[port]/[tcp|udp|sctp]" (e.g. "22/tcp"). Value must be an empty object.
     *                     If an empty-object value(which is JSON equivalent of {}, empty curly brackets) is needed, use {@link EmptyObject}.
     * @return <code>Config</code> object with initialized <code>exposedPorts</code> field.
     */
    public Config setExposedPorts(Map<String, Object> exposedPorts) {
        this.exposedPorts = exposedPorts;
        return this;
    }

    public boolean isTty() {
        return tty;
    }

    /**
     * Allocate pseudo-TTY to a container or not. If the tty is allocated, Stdout/err named-pipes are automatically generated
     * even if {@link Config#isAttachStdout()} and {@link Config#isAttachStderr()} are set to <strong>false</strong>.
     * This method is equivalent of <strong>docker run -t (--tty) ...</strong> when the parameter is true.
     *
     * @param tty Set this parameter to <strong>true </strong> to allocate tty. Default: <strong>false</strong>
     * @return <code>Config</code> object with initialized <code>tty</code> field.
     */
    public Config setTty(boolean tty) {
        this.tty = tty;
        return this;
    }

    public boolean isOpenStdin() {
        return openStdin;
    }

    /**
     * Open stdin or not.
     * If stdin is opened, Stdin/out/err named-pipes are automatically generated, even if {@link Config#isAttachStdin()},
     * {@link Config#isAttachStdout()}, and {@link Config#isAttachStderr()} are set to <strong>false</strong>.
     * This method is equivalent of <strong>docker run -i (--interactive)</strong> when the parameter is true.
     *
     * @param openStdin Set this parameter to <strong>true</strong> to open stdin. Default: <Strong>false</Strong>.
     * @return <code>Config</code> object with initialized <code>openStdin</code> field.
     */
    public Config setOpenStdin(boolean openStdin) {
        this.openStdin = openStdin;
        return this;
    }

    public boolean isStdinOnce() {
        return stdinOnce;
    }

    /**
     * Close stdin after the attached client disconnects.
     * @param stdinOnce Set this parameter to <strong>true</strong> if you want to close stdin when user disconnects. Default: <strong> false</strong>.
     * @return <code>Config</code> object with initialized <code>openStdin</code> field.
     */
    public Config setStdinOnce(boolean stdinOnce) {
        this.stdinOnce = stdinOnce;
        return this;
    }

    public String[] getEnv() {
        return env;
    }

    /**
     * Set a list of environment variables.
     * For example, ["PATH=...", "FOO=BAR"]
     * @param env a list of environment variables to set inside the container.
     * @return <code>Config</code> object with initialized <code>env</code> field.
     */
    public Config setEnv(String[] env) {
        this.env = env;
        return this;
    }

    public String[] getCmd() {
        return cmd;
    }

    /**
     * Set commands to run.
     * @param cmd String array of commands to run.
     * @return <code>Config</code> object with initialized <code>cmd</code> field.
     */
    public Config setCmd(String[] cmd) {
        this.cmd = cmd;
        return this;
    }

    public HealthConfig getHealthConfig() {
        return healthConfig;
    }

    /**
     * A test to perform to check if the container is healthy.
     * @param healthConfig A {@link HealthConfig} object. You need to instantiate and configure {@link HealthConfig} class first.
     * @return <code>Config</code> object with initialized <code>healthConfig</code> field.
     */
    public Config setHealthConfig(HealthConfig healthConfig) {
        this.healthConfig = healthConfig;
        return this;
    }

    public boolean isArgsEscaped() {
        return argsEscaped;
    }

    /**
     * Windows-only configuration but non-Windows may set this field, but it will have any effects.
     * @param argsEscaped Command is already escaped.
     * @return <code>Config</code> object with initialized <code>argsEscaped</code> field.
     */
    public Config setArgsEscaped(boolean argsEscaped) {
        this.argsEscaped = argsEscaped;
        return this;
    }


    /**
     * Get image name that was used when the container was created.
     * @return Image name string.
     */
    public String getImage() {
        return image;
    }

    /**
     * Set name of the image to use when creating a container.
     * @param image A name of image to use when creating a container.
     * @return <code>Config</code> object with initialized <code>image</code> field.
     */
    public Config setImage(String image) {
        this.image = image;
        return this;
    }

    public Map<String, String> getVolumes() {
        return volumes;
    }

    public Config setVolumes(Map<String, String> volumes) {
        this.volumes = volumes;
        return this;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public Config setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
        return this;
    }

    public String[] getEntryPoint() {
        return entryPoint;
    }

    public Config setEntryPoint(String[] entryPoint) {
        this.entryPoint = entryPoint;
        return this;
    }

    public boolean isNetworkDisabled() {
        return networkDisabled;
    }

    public Config setNetworkDisabled(boolean networkDisabled) {
        this.networkDisabled = networkDisabled;
        return this;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public Config setMacAddress(String macAddress) {
        this.macAddress = macAddress;
        return this;
    }

    public String[] getOnBuild() {
        return onBuild;
    }

    public Config setOnBuild(String[] onBuild) {
        this.onBuild = onBuild;
        return this;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public Config setLabels(Map<String, String> labels) {
        this.labels = labels;
        return this;
    }

    public String getStopSignal() {
        return stopSignal;
    }

    public Config setStopSignal(String stopSignal) {
        this.stopSignal = stopSignal;
        return this;
    }

    public int getStopTimeout() {
        return stopTimeout;
    }

    public Config setStopTimeout(int stopTimeout) {
        this.stopTimeout = stopTimeout;
        return this;
    }

    public String[] getShell() {
        return shell;
    }

    public Config setShell(String[] shell) {
        this.shell = shell;
        return this;
    }

    public HostConfig getHostConfig() {
        return hostConfig;
    }

    public Config setHostConfig(HostConfig hostConfig) {
        this.hostConfig = hostConfig;
        return this;
    }

    public NetworkingConfig getNetworkingConfig() {
        return networkingConfig;
    }

    public Config setNetworkingConfig(NetworkingConfig networkingConfig) {
        this.networkingConfig = networkingConfig;
        return this;
    }
}