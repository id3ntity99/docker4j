package client.json.configs.config;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * An empty object to create empty bracket({}) for JSON mapping.
 */
@JsonSerialize
public class EmptyObject {
    public EmptyObject() {}
}
