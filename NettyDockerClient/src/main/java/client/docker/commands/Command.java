package client.docker.commands;

import client.docker.commands.exceptions.CommandBuildException;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.netty.handler.codec.http.FullHttpRequest;

public abstract class Command {
    protected static final ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    protected static final ObjectWriter writer = mapper.writer()
            .withDefaultPrettyPrinter();

    public abstract FullHttpRequest build() throws CommandBuildException;
}
