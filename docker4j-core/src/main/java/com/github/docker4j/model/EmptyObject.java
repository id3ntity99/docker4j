package com.github.docker4j.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * An empty object to create empty bracket({}) for JSON.
 */
@JsonSerialize
public class EmptyObject {
    public EmptyObject() {}
}
