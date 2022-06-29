package client.docker.configs.exec;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExecCreateConfig {
    @JsonProperty("AttachStdin")
    private boolean isAttachStdin;
    @JsonProperty("AttachStdout")
    private boolean isAttachStdout;
    @JsonProperty("AttachStderr")
    private boolean isAttachStderr;
    @JsonProperty("DetachKeys")
    private String detachKeys;
    @JsonProperty("Tty")
    private boolean tty;
    @JsonProperty("Env")
    private String[] env;
    @JsonProperty("Cmd")
    private String[] cmd;
    @JsonProperty("Privileged")
    private boolean isPrivileged = false;
    @JsonProperty("User")
    private String user;
    @JsonProperty("WorkingDir")
    private String workingDir;

    public boolean isAttachStdin() {
        return isAttachStdin;
    }

    public ExecCreateConfig setAttachStdin(boolean attachStdin) {
        isAttachStdin = attachStdin;
        return this;
    }

    public boolean isAttachStdout() {
        return isAttachStdout;
    }

    public ExecCreateConfig setAttachStdout(boolean attachStdout) {
        isAttachStdout = attachStdout;
        return this;
    }

    public boolean isAttachStderr() {
        return isAttachStderr;
    }

    public ExecCreateConfig setAttachStderr(boolean attachStderr) {
        isAttachStderr = attachStderr;
        return this;
    }

    public String getDetachKeys() {
        return detachKeys;
    }

    public ExecCreateConfig setDetachKeys(String detachKeys) {
        this.detachKeys = detachKeys;
        return this;
    }

    public boolean isTty() {
        return tty;
    }

    public ExecCreateConfig setTty(boolean tty) {
        this.tty = tty;
        return this;
    }

    public String[] getEnv() {
        return env;
    }

    public ExecCreateConfig setEnv(String[] env) {
        this.env = env;
        return this;
    }

    public String[] getCmd() {
        return cmd;
    }

    public ExecCreateConfig setCmd(String[] cmd) {
        this.cmd = cmd;
        return this;
    }

    public boolean isPrivileged() {
        return isPrivileged;
    }

    public ExecCreateConfig setPrivileged(boolean privileged) {
        isPrivileged = privileged;
        return this;
    }

    public String getUser() {
        return user;
    }

    public ExecCreateConfig setUser(String user) {
        this.user = user;
        return this;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public ExecCreateConfig setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
        return this;
    }
}
