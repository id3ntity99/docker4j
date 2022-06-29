package client.docker.configs.config;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper class to get/set volumes.
 * This class creates a hash map internally, so the users only need to set the map
 * by invoking {@link Volumes#setVolumeMap(String)} method.
 */
public class Volumes {
    @JsonValue
    private Map<String, EmptyObject> volumeMap = new HashMap<>();

    /**
     * Sets a mount-point paths inside the container.
     *
     * @param path A mount-point inside the container.
     */
    public void setVolumeMap(String path) {
        volumeMap.put(path, new EmptyObject());
    }

    /**
     * Get the hash map.
     * @return Hash map of path-empty object.
     */
    public Map<String, EmptyObject> getVolumeMap() {
        return volumeMap;
    }
}
