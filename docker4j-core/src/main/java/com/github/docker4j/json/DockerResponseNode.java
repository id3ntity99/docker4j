package com.github.docker4j.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.docker4j.DockerResponse;

import java.util.ArrayList;
import java.util.List;

public class DockerResponseNode {
    private final List<DockerResponse> responses = new ArrayList<>();
    private final ObjectNode node = new ObjectMapper().createObjectNode();

    public void add(DockerResponse response) {
        responses.add(response);
    }

    public DockerResponse get(int i) {
        return responses.get(i);
    }

    public DockerResponse last() {
        return responses.get(responses.size() - 1);
    }

    public List<DockerResponse> search(Class<? extends DockerResponse> type) {
        List<DockerResponse> filtered = new ArrayList<>();
        for (DockerResponse response : responses) {
            if (response.getClass() == type)
                filtered.add(response);
        }
        return filtered;
    }

    public int size() {
        return responses.size();
    }

    public void clear() {
        responses.clear();
    }
}
