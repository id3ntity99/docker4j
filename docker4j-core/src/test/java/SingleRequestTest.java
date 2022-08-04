import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.docker4j.*;
import com.github.docker4j.exceptions.DockerResponseException;
import com.github.docker4j.json.DockerResponseNode;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Promise;
import org.junit.jupiter.api.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SyncRequestTest {
    private static DockerClient client;
    private static DockerResponseNode globalNode;
    private static final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    public static void setup() throws Exception {
        client = new DefaultDockerClient().withAddress("localhost", 2375)
                .withEventLoopGroup(eventLoopGroup)
                .withOutChannelClass(NioSocketChannel.class);
        client.connect().sync().addListener((ChannelFutureListener) future -> assertTrue(future.isSuccess()));
    }

    @AfterEach
    public void printNode() throws JsonProcessingException {
        for (int i = 0 ; i < globalNode.size(); i++) {
            DockerResponse current = globalNode.get(i);
            String responseString = mapper.writeValueAsString(current);
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
    void createContainer_successful() throws Exception {
        DockerHandler request = new CreateContainerHandler.Builder()
                .withImage("alpine")
                .withTty(true)
                .withStopTimeout(1)
                .build();
        globalNode = client.add(request).request().get();
        assertEquals(1, globalNode.size());
    }

    @Test
    @Order(2)
    void createContainer_unsuccessful() throws Exception {
        DockerHandler request = new CreateContainerHandler.Builder().withImage("#(!)*").build();
        Promise<DockerResponseNode> promise = client.add(request).request();
        assertThrows(DockerResponseException.class, promise::sync);
        assertEquals(1, globalNode.size());
    }

    @Test
    @Order(3)
    void startContainer_successful() throws Exception {
        DockerHandler startRequest = new StartContainerHandler.Builder().build();
        client.add(startRequest).request().sync();
        assertEquals(1, globalNode.size());
    }

    @Test
    @Order(4)
    void stopContainer_successful() throws Exception {
        DockerHandler stopHandler = new StopContainerHandler.Builder().withWaitTime(1).build();
        client.add(stopHandler).request().sync();
        assertEquals(1, globalNode.size());
    }

    @Test
    @Order(5)
    void removeContainer_successful() throws Exception {
        DockerHandler removeHandler = new RemoveContainerHandler.Builder()
                .withVolumeRemove(false)
                .withForceRemove(false)
                .withLinkRemove(false)
                .build();
        client.add(removeHandler).request().sync();
        assertEquals(1, globalNode.size());
    }

    @Test
    @Order(6)
    void startContainer_unsuccessful() throws Exception {
        globalNode.setInternal("_container_id", "gibberishContainerId");
        DockerHandler startHandler = new StartContainerHandler.Builder().build();
        Promise<DockerResponseNode> promise = client.add(startHandler).request();
        assertThrows(DockerResponseException.class, promise::sync);
        assertEquals(1, globalNode.size());
    }

    @Test
    @Order(7)
    void stopContainer_unsuccessful() throws Exception {
        DockerHandler stopHandler = new StopContainerHandler.Builder().withWaitTime(1).build();
        Promise<DockerResponseNode> promise = client.add(stopHandler).request();
        assertThrows(DockerResponseException.class, promise::sync);
        assertEquals(1, globalNode.size());
    }

    @Test
    @Order(8)
    void removeContainer_unsuccessful() throws Exception {
        DockerHandler removeHandler = new RemoveContainerHandler.Builder().build();
        Promise<DockerResponseNode> promise = client.add(removeHandler).request();
        assertThrows(DockerResponseException.class, promise::sync);
        assertEquals(1, globalNode.size());
    }

    @Test
    @Order(9)
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
        assertDoesNotThrow(promise::sync);
        assertEquals(1, node.size());
        for (int i = 0 ; i < node.size(); i++) {
            DockerResponse current = node.get(i);
            String responseString = mapper.writeValueAsString(current);
            System.out.println(responseString);
            System.out.println(current.toString());
        }
    }

    @Test
    @Order(10)
    void chainRequest_sameInstances_successful() throws Exception {
        globalNode.clear();
        DockerHandler createHandler = new CreateContainerHandler.Builder().withImage("alpine").build();
        DockerHandler removeHandler = new RemoveContainerHandler.Builder().build();
        Promise<DockerResponseNode> promise = client.add(createHandler)
                .add(removeHandler)
                .add(createHandler)
                .add(removeHandler)
                .request();
        assertDoesNotThrow(promise::sync);
        assertEquals(2, globalNode.size());
    }

    @Test
    @Order(11)
    void createContainer_fullConfig_successful() throws Exception {
        // TODO to perform this test, we need to implement InspectContainerHandler
        //  to check whether the configuration works and applied properly.
        globalNode.clear();
        DockerHandler inspectHandler = new InspectContainerHandler.Builder().withSize(false).build();
        DockerHandler removeHandler = new RemoveContainerHandler.Builder().build();
        DockerHandler createContainer = new CreateContainerHandler.Builder()
                .withImage("alpine")
                .build();
        client.add(createContainer).add(inspectHandler).add(removeHandler).request().get();
        assertEquals(3, globalNode.size());
    }
}
