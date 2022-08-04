package com.github.docker4j.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JacksonHelper {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ObjectWriter WRITER = MAPPER.writer();

    private JacksonHelper() {
    }

    public static String writeValueAsString(Object value) throws JsonProcessingException {
        return WRITER.writeValueAsString(value);
    }
}
