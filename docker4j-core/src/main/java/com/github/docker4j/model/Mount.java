package com.github.docker4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.List;

public class Mount {
    @JsonProperty("Target")
    private String target;
    @JsonProperty("Source")
    private String source;
    @JsonProperty("Type")
    private String type;
    @JsonProperty("ReadOnly")
    private boolean readOnly;
    @JsonProperty("Consistency")
    private String consistency;
    @JsonProperty("BindOptions")
    private BindOptions bindOptions;
    @JsonProperty("VolumeOptions")
    private VolumeOptions volumeOptions;
    @JsonProperty("TmpfsOptions")
    private TmpfsOptions tmpfsOptions;

    public Mount setType(String type) throws IllegalArgumentException {
        String[] validTypes = {"bind", "volume", "tmpfs", "npipe"};
        List<String> validTypesList = Arrays.asList(validTypes);
        if (validTypesList.contains(type)) {
            this.type = type;
        } else {
            String errMsg = String.format("Not a valid mount type. Valid mount types are: %s",
                    Arrays.toString(validTypes));
            throw new IllegalArgumentException(errMsg);
        }
        return this;
    }

    public Mount setTarget(String target) {
        this.target = target;
        return this;
    }

    public Mount setSource(String source) {
        this.source = source;
        return this;
    }

    public Mount setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        return this;
    }

    public Mount setConsistency(String consistency) {
        this.consistency = consistency;
        return this;
    }

    public Mount setBindOptions(BindOptions bindOptions) {
        this.bindOptions = bindOptions;
        return this;
    }

    public Mount setTmpfsOptions(TmpfsOptions tmpfsOptions) {
        this.tmpfsOptions = tmpfsOptions;
        return this;
    }

    public Mount setVolumeOptions(VolumeOptions volumeOptions) {
        this.volumeOptions = volumeOptions;
        return this;
    }

    public String getType() {
        return type;
    }

    public String getConsistency() {
        return consistency;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public boolean getReadOnly() {
        return readOnly;
    }

    public BindOptions getBindOptions() {
        return bindOptions;
    }

    public TmpfsOptions getTmpfsOptions() {
        return tmpfsOptions;
    }

    public VolumeOptions getVolumeOptions() {
        return volumeOptions;
    }
}