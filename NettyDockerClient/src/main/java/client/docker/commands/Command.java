package client.docker.commands;

import client.docker.commands.exceptions.DockerRequestException;
import client.docker.dockerclient.proxy.NettyDockerClient;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public abstract class Command<T1, T2> {
    protected static final ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    protected static final ObjectWriter writer = mapper.writer()
            .withDefaultPrettyPrinter();


    public abstract T1 withDockerClient(NettyDockerClient nettyDockerClient);
    public abstract T2 exec() throws DockerRequestException;
}
