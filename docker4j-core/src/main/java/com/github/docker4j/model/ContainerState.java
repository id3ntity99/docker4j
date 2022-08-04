package com.github.docker4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ContainerState {
    @JsonProperty("Status")
    private String status;
    @JsonProperty("Running")
    private boolean running;
    @JsonProperty("Paused")
    private boolean paused;
    @JsonProperty("Restarting")
    private boolean restarting;
    @JsonProperty("OOMKilled")
    private boolean oomKilled;
    @JsonProperty("Dead")
    private boolean dead;
    @JsonProperty("Pid")
    private int pid;
    @JsonProperty("ExitCode")
    private int exitCode;
    @JsonProperty("Error")
    private String error;
    @JsonProperty("StartedAt")
    private String startedAt;
    @JsonProperty("FinishedAt")
    private String finishedAt;
    @JsonProperty("Health")
    private Health health;

    public String getStatus() {
        return status;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isRestarting() {
        return restarting;
    }

    public boolean isOomKilled() {
        return oomKilled;
    }

    public boolean isDead() {
        return dead;
    }

    public int getPid() {
        return pid;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getError() {
        return error;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public String getFinishedAt() {
        return finishedAt;
    }

    public Health getHealth() {
        return health;
    }
}
