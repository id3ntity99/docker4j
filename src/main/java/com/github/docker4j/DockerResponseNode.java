package com.github.docker4j;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * This class contains multiple responses that the {@link DockerHandler}s received.
 * Also, this class maintains some information for internal uses, such as container id or exec id, for the subsequent requests.
 * All internally-used keys starts with the underscore(_). (e.g. _container_id, _exec_id)
 * Note that, if there are multiple successive handlers with the same purpose, added into the {@link DockerClient},
 * then the internal keys will be overwritten to the latest values. <br/>
 * For example, let's say that you added two {@link CreateContainerHandler}s successively.
 * The first container id, abc123, will be added into "_container_id" field of {@link DockerResponseNode}.
 * And the second container id, xyz789, will <strong>overwrite</strong> the previous value of the _container_id, whose value was abc123 in this case.
 */
public class DockerResponseNode {
    private final ObjectNode node = new ObjectMapper().createObjectNode();

    void add(String key, JsonNode jsonNode) {
        node.set(key, jsonNode);
    }

    void add(String key, String value) {
        node.put(key, value);
    }

    public String find(String key) {
        return node.findValue(key).asText();
    }

    @Override
    public String toString() {
        return node.toPrettyString();
    }
}
