package client.docker.model;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper class to get/set exposed-ports.
 * Used by {@link Config}.
 * This class creates hash map internally, so the users only need to set the map
 * by invoking {@link ExposedPorts#setExposedPortsMap(String)} method.
 */
public class ExposedPorts {
    @JsonValue
    private Map<String, EmptyObject> exposedPortsMap = new HashMap<>();

    /**
     *
     * Sets a exposed ports map.
     * @param key A port to expose. The key format must be "[port]/[tcp|udp|sctp]" (e.g. "22/tcp").
     */
    public void setExposedPortsMap(String key) {
        exposedPortsMap.put(key, new EmptyObject());
    }

    /**
     * Get the hash map.
     * @return Hash map of port-empty object.
     */
    public Map<String, EmptyObject> getExposedPortsMap() {
        return exposedPortsMap;
    }
}
