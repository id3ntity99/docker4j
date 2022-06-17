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
    private Map<String, Object> volumes;
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
    private int stopTimeout = 10;
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
     * @param exposedPorts Key must satisfy the form "[port]/[tcp|udp|sctp]" (e.g. "22/tcp").
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
     *
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
     *
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
     *
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
     *
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
     *
     * @param argsEscaped Command is already escaped.
     * @return <code>Config</code> object with initialized <code>argsEscaped</code> field.
     */
    public Config setArgsEscaped(boolean argsEscaped) {
        this.argsEscaped = argsEscaped;
        return this;
    }


    /**
     * Get image name that was used when the container was created.
     *
     * @return Image name string.
     */
    public String getImage() {
        return image;
    }

    /**
     * Set name of the image to use when creating a container.
     *
     * @param image A name of image to use when creating  a container.
     * @return <code>Config</code> object with initialized <code>image</code> field.
     */
    public Config setImage(String image) {
        this.image = image;
        return this;
    }

    public Map<String, Object> getVolumes() {
        return volumes;
    }

    /**
     * Set mount point paths inside the container. Use {@link EmptyObject} for hashmap value
     * to map to JSON empty curly brackets({}).
     *
     * @param volumes A hashmap which takes mount point paths inside the container as a String key
     *                and an empty object as a value.
     * @return <code>Config</code> object with initialized <code>volumes</code> field.
     */
    public Config setVolumes(Map<String, Object> volumes) {
        this.volumes = volumes;
        return this;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    /**
     * Set working directory inside the container.
     *
     * @param workingDir The working directory for commands to run in.
     * @return <code>Config</code> object with initialized <code>workingDirr</code> field.
     */
    public Config setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
        return this;
    }

    public String[] getEntryPoint() {
        return entryPoint;
    }

    /**
     * Set entrypoint for container.
     *
     * @param entryPoint The entrypoint(s) for the container as a string or an array of strings.
     *                   An entrypoint specifies the executable, which should run when a container is started from
     *                   the Docker image.
     * @return <code>Config</code> object with initialized <code>entrypoint</code> field.
     */
    public Config setEntryPoint(String[] entryPoint) {
        this.entryPoint = entryPoint;
        return this;
    }

    public boolean isNetworkDisabled() {
        return networkDisabled;
    }

    /**
     * Disable networking for the container.
     *
     * @param networkDisabled
     * @return <code>Config</code> object with initialized <code>networkDisabled</code> field.
     */
    public Config setNetworkDisabled(boolean networkDisabled) {
        this.networkDisabled = networkDisabled;
        return this;
    }

    public String getMacAddress() {
        return macAddress;
    }

    /**
     * Set MAC address of the container.
     *
     * @param macAddress
     * @return <code>Config</code> object with initialized <code>macAddress</code> field.
     */
    public Config setMacAddress(String macAddress) {
        this.macAddress = macAddress;
        return this;
    }

    public String[] getOnBuild() {
        return onBuild;
    }

    /**
     * Set ONBUILD metadata that were defined in the image's Dockerfile.
     * ONBUILD instruction adds to the image a <strong>trigger</strong> instruction to be executed at a later time.
     *
     * @param onBuild Array of Strings for ONBUILD instructions.
     * @return <code>Config</code> object with initialized <code>onBuild</code> field.
     * @see <a href="https://docs.docker.com/engine/reference/builder/#onbuild">ONBUILD</a>
     */
    public Config setOnBuild(String[] onBuild) {
        this.onBuild = onBuild;
        return this;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    /**
     * Set user-defined metadata.
     *
     * @param labels Map of user-defined metadata.
     * @return <code>Config</code> object with initialized <code>labels</code> field.
     */
    public Config setLabels(Map<String, String> labels) {
        this.labels = labels;
        return this;
    }

    public String getStopSignal() {
        return stopSignal;
    }

    /**
     * Set a signal to stop a container.
     *
     * @param stopSignal Stop signal as a String.
     * @return <code>Config</code> object with initialized <code>stopSignal</code> field.
     */
    public Config setStopSignal(String stopSignal) {
        this.stopSignal = stopSignal;
        return this;
    }

    public int getStopTimeout() {
        return stopTimeout;
    }

    /**
     * Set timeout to stop a container in <strong>seconds</strong>.
     * If the stop timeout set to 20, it takes 20 seconds for the container to stop.
     * Default: 10.
     *
     * @param stopTimeout Timeout to stop a cotainer in seconds.
     * @return <code>Config</code> object with initialized <code>stopTimeout</code> field.
     */
    public Config setStopTimeout(int stopTimeout) {
        this.stopTimeout = stopTimeout;
        return this;
    }

    public String[] getShell() {
        return shell;
    }

    /**
     * Set the shell commands used when RUN, CMD, and ENTRYPOINT as an array of string.
     *
     * @param shell The shell commands that was used as an array of string.
     * @return <code>Config</code> object with initialized <code>shell</code> field.
     */
    public Config setShell(String[] shell) {
        this.shell = shell;
        return this;
    }

    public HostConfig getHostConfig() {
        return hostConfig;
    }


    /**
     * Set {@link HostConfig}. Use {@link HostConfig} to create {@link HostConfig} object, and to configure it.
     * Then pass the instance to the parameter of this method.
     * Note that, however, you can create and configure the {@link HostConfig} and pass it to the parameter,
     * instantiation of {@link Config} will instantiate {@link HostConfig} and init the corresponding field.
     * with that base configuration.
     *
     * @param hostConfig Container configuration that depends on the host we are running on.
     * @return <code>Config</code> object with initialized <code>hostConfig</code> field.
     */
    public Config setHostConfig(HostConfig hostConfig) {
        this.hostConfig = hostConfig;
        return this;
    }

    public NetworkingConfig getNetworkingConfig() {
        return networkingConfig;
    }

    /**
     * Set {@link NetworkingConfig}, which is used for the networking configs specified in
     * the <strong>docker create</strong> and <strong>docker network create</strong>.
     * Use {@link NetworkingConfig} to create {@link NetworkingConfig} object, and to configure it.
     * Then pass the instance to the parameter of this method.
     * Note that, however, even though you can create {@link NetworkingConfig} object, configure it,
     * and pass it to this method's parameter, instantiation of {@link Config} will instantiate
     * {@link NetworkingConfig} and init corresponding field with that base configuration.
     *
     * @param networkingConfig Represents the container's networking configuration.
     * @return <code>Config</code> object with initialized <code>networkingConfig</code> field.
     */
    public Config setNetworkingConfig(NetworkingConfig networkingConfig) {
        this.networkingConfig = networkingConfig;
        return this;
    }
}