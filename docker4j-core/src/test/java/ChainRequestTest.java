import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.docker4j.*;
import com.github.docker4j.json.DockerResponseNode;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Promise;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ChainRequestTest {
    private static DockerClient client;
    private static final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    private final ObjectMapper mapper = new ObjectMapper();
    private DockerResponseNode globalNode;

    @BeforeAll
    public static void setup() throws Exception {
        client = new DefaultDockerClient().withAddress("localhost", 2375)
                .withEventLoopGroup(eventLoopGroup)
                .withOutChannelClass(NioSocketChannel.class);
        client.connect().sync().addListener((ChannelFutureListener) future -> assertTrue(future.isSuccess()));
    }

    @AfterEach
    public void printNode() throws JsonProcessingException {
        for (int i = 0; i < globalNode.size(); i++) {
            DockerResponse current = globalNode.get(i);
            String responseString = mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(current);
            System.out.println(responseString);
            System.out.println(current.toString());
        }
    }

    @AfterAll
    public static void tearDown() throws IOException {
        client.close();
    }

    @Test
    @Order(1)
    void chainRequest_successful() throws Exception {
        client = new DefaultDockerClient().withAddress("localhost", 2375)
                .withEventLoopGroup(eventLoopGroup)
                .withOutChannelClass(NioSocketChannel.class);
        client.connect().sync().addListener((ChannelFutureListener) future -> assertTrue(future.isSuccess()));
        DockerHandler createHandler = new CreateContainerHandler.Builder()
                .withImage("alpine")
                .withStopTimeout(1)
                .withTty(true)
                .build();
        DockerHandler startHandler = new StartContainerHandler.Builder().build();
        DockerHandler stopHandler = new StopContainerHandler.Builder()
                .withWaitTime(1)
                .build();
        DockerHandler removeHandler = new RemoveContainerHandler.Builder().build();
        Promise<DockerResponseNode> promise = client.add(createHandler)
                .add(startHandler)
                .add(stopHandler)
                .add(removeHandler)
                .request();
        DockerResponseNode node = promise.get();
        globalNode=node;
        assertDoesNotThrow(promise::sync);
        assertEquals(1, node.size());
    }

    @Test
    @Order(2)
    void chainRequest_sameInstances_successful() throws Exception {
        DockerHandler createHandler = new CreateContainerHandler.Builder().withImage("alpine").build();
        DockerHandler removeHandler = new RemoveContainerHandler.Builder().build();
        Promise<DockerResponseNode> promise = client.add(createHandler)
                .add(removeHandler)
                .add(createHandler)
                .add(removeHandler)
                .request();
        assertDoesNotThrow(promise::sync);
        DockerResponseNode node = promise.get();
        globalNode=node;
        assertEquals(2, node.size());
    }

    @Test
    @Order(3)
    void createContainer_fullConfig_successful() throws Exception {
        //  TODO check whether the configuration works and applied properly.
        DockerHandler inspectHandler = new InspectContainerHandler.Builder().withSize(false).build();
        DockerHandler removeHandler = new RemoveContainerHandler.Builder().build();
        DockerHandler createContainer = new CreateContainerHandler.Builder()
                .withImage("alpine")
                .build();
        DockerResponseNode node = client.add(createContainer)
                .add(inspectHandler)
                .add(removeHandler)
                .request()
                .get();
        globalNode=node;
        assertEquals(2, node.size());
    }
}
