package docker4j.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import docker4j.CreateContainerHandler;
import docker4j.DockerClient;
import docker4j.DockerHandler;

/**
 * This class contains multiple responses that the {@link DockerHandler}s received.
 * Also, this class maintains some information for internal uses in {@link #internals} field, such as container id or
 * exec id, for the subsequent requests. All internally-used keys starts with the underscore(_). (e.g. _container_id, _exec_id)
 * Note that, if there are multiple successive handlers with the same purpose added into the {@link DockerClient},
 * then the internally-used keys will be overwritten to the latest values. <br/>
 * For example, let's say that you added two {@link CreateContainerHandler}s successively.
 * The first container id, abc123, will be added into "_container_id" field.
 * And the second container id, xyz789, will <strong>overwrite</strong> the previous value of the _container_id, which
 * was abc123 in this example. Similarly, the previous responses are stored in the {@link #histories} field as a json array
 * for the users to debug or simply reference the whole responses. The only difference between {@link #internals} and {@link #histories}
 * is that the histories will push new response into the array instead of overwriting.
 */
public class DockerResponseNode {
    /**
     * A constant that holds string-key name, "_container_id"
     */
    public static final String CONTAINER_ID = "_container_id";

    /**
     * A constant that holds string-key name, "_exec_id"
     */
    public static final String EXEC_ID = "_exec_id";

    /**
     * A constant that holds string-key name, "_error"
     */
    public static final String ERROR = "_error";

    /**
     * The top-most JSON {@link ObjectNode} who contains {@link #internals} and {@link #histories}
     */
    private final ObjectNode node = new ObjectMapper().createObjectNode();


    /**
     * One of the child JSON property of {@link #node} whose name is "_internals".<br/>
     * This {@link ObjectNode} contains values who are used internally.
     */
    private final ObjectNode internals = node.putObject("_internals")
            .putNull(CONTAINER_ID)
            .putNull(EXEC_ID)
            .putNull(ERROR);

    /**
     * One of the child JSON property of {@link #node} whose name is "response_histories"<br/>
     * This {@link ArrayNode} contains the histories of responses, which were received by {@link DockerHandler}.
     */
    private final ArrayNode histories = node.putArray("response_histories");

    /**
     * Add a response into the {@link #histories}
     *
     * @param jsonNode A {@link JsonNode} to add
     */
    public void setResponse(JsonNode jsonNode) {
        histories.add(jsonNode);
    }

    /**
     * Set a value of the key in the {@link #internals}
     *
     * @param key   A key-name to set or update its value
     * @param value A value to set for the given key.
     */
    public void setInternal(String key, String value) {
        internals.put(key, value);
    }

    /**
     * Get all {@link #histories} as string.
     *
     * @return String of {@link #histories}.
     */
    public String getHistories() {
        return histories.toPrettyString();
    }

    /**
     * Find and return value in {@link #histories} by key.
     *
     * @param key A key name of target
     * @return The value found by key
     */
    public String getHistory(String key) {
        return histories.findValue(key).asText();
    }

    /**
     * Get a history as {@link JsonNode}
     *
     * @param index Index number of target history
     * @return A {@link JsonNode} found by index
     */
    public JsonNode getHistory(int index) {
        return histories.get(index);
    }

    /**
     * Get internal values as string
     *
     * @return The internal values as string
     */
    public String getInternals() {
        return internals.toPrettyString();
    }

    /**
     * Get an internal value by key
     *
     * @param key A key name of target
     * @return An internal value as string
     */
    public String getInternal(String key) {
        return internals.get(key).asText();
    }

    /**
     * Get size or length of the {@link #histories}
     *
     * @return Size of {@link #histories}
     */
    public int historySize() {
        return histories.size();
    }


    /**
     * Clear the {@link #internals} and {@link #histories} to null
     */
    public void clear() {
        internals.removeAll();
        histories.removeAll();
        internals.putNull(CONTAINER_ID)
                .putNull(EXEC_ID)
                .putNull(EXEC_ID);
    }


    /**
     * Get the String of entire {@link node}.
     * @return
     */
    @Override
    public String toString() {
        return node.toPrettyString();
    }
}
