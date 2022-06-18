package client.nettyClient;

import client.docker.commands.CreateContainerCmd;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class ClientRunner {
    public static void main(String[] args) throws Exception {
        new CreateContainerCmd().withTty(true)
                .withImage("runner-image")
                .exec();
    }
}
