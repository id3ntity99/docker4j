package com.github.docker4j.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TmpfsOptions {
    @JsonProperty("SizeBytes")
    private long sizeBytes;
    @JsonProperty("Mode")
    private int mode;

    public TmpfsOptions setSizeBytes(long sizeBytes) {
        this.sizeBytes = sizeBytes;
        return this;
    }

    public TmpfsOptions setMode(int mode) {
        this.mode = mode;
        return this;
    }

    public int getMode() {
        return mode;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }
}
