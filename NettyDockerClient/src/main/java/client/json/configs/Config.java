package client.json.configs;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Used for creation of container and inspection?
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
    private boolean attachStdout = false;

    @JsonProperty("AttachStderr")
    private boolean attachStderr = false;

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

    @JsonProperty("Image")
    private String image = "";

    @JsonProperty("WorkingDir")
    private String workingDir = "";

    @JsonProperty("Entrypoint")
    private String[] entryPoint = null;

    @JsonProperty("OnBuild")
    private String[] onBuild = null;

    @JsonProperty("Labels")
    private String labels = null;

    public Config withAttachStdin(boolean attachStdin) {
        this.attachStdin = attachStdin;
        return this;
    }

    public Config withAttachStdout(boolean attachStdout) {
        this.attachStdout = attachStdout;
        return this;
    }

    public Config withAttachStderr(boolean attachStderr) {
        this.attachStderr = attachStderr;
        return this;
    }

    public Config withTty(boolean tty) {
        this.tty = tty;
        return this;
    }

    public Config withImage(String image) {
        this.image = image;
        return this;
    }

    public Config withCmd(String[] cmd) {
        this.cmd = cmd;
        return this;
    }

    public Config withWorkingDir(String workingDir) {
        this.workingDir = workingDir;
        return this;
    }

    public Config withUser(String user) {
        this.user = user;
        return this;
    }
}
