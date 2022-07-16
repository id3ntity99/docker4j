package client.docker;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * This class contains multiple responses that are related to the {@link DockerRequest}s given to the {@link RequestLinker}.
 * Also, this class maintains some information, such as container id and exec id, for the subsequent requests.
 * User cannot access this information because they are used internally.
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
