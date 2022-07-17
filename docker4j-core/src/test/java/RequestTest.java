import com.github.docker4j.*;
import com.github.docker4j.exceptions.DockerResponseException;
import com.github.docker4j.exceptions.DuplicationException;
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
class RequestTest {
    private static DockerClient client;
    private static DockerResponseNode globalNode;
    private static final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    @BeforeAll
    public static void setup() throws Exception {
        client = new DefaultDockerClient().withAddress("localhost", 2375)
                .withEventLoopGroup(eventLoopGroup)
                .withOutChannelClass(NioSocketChannel.class);
        client.connect().sync().addListener((ChannelFutureListener) future -> assertTrue(future.isSuccess()));
    }

    @AfterEach
    public void printNode() {
        System.out.println(globalNode);
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
        String containerId = globalNode.getInternal("_container_id");
        assertFalse(containerId.isEmpty());
        assertFalse(globalNode.getHistories().isEmpty());
        assertFalse(globalNode.getInternal("_container_id").isEmpty());
    }

    @Test
    @Order(2)
    void createContainer_unsuccessful() throws Exception{
        DockerHandler request = new CreateContainerHandler.Builder().withImage("#(!)*").build();
        Promise<DockerResponseNode> promise = client.add(request).request();
        assertThrows(DockerResponseException.class, promise::sync);
        assertFalse(globalNode.getInternal("_error").isEmpty());
    }

    @Test
    @Order(3)
    void startContainer_successful() throws Exception {
        DockerHandler startRequest = new StartContainerHandler.Builder().build();
        client.add(startRequest).request().sync();
        assertFalse(globalNode.getHistories().isEmpty());
        assertEquals(2, globalNode.historySize());
    }

    @Test
    @Order(4)
    void stopContainer_successful() throws Exception {
        DockerHandler stopHandler = new StopContainerHandler.Builder().withWaitTime(1).build();
        client.add(stopHandler).request().sync();
        assertEquals(3, globalNode.historySize());
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
        assertEquals(4, globalNode.historySize());
    }

    @Test
    @Order(6)
    void startContainer_unsuccessful() throws Exception {
        globalNode.setInternal("_container_id", "gibberishContainerId");
        DockerHandler startHandler = new StartContainerHandler.Builder().build();
        Promise<DockerResponseNode> promise = client.add(startHandler).request();
        assertThrows(DockerResponseException.class, promise::sync);

    }

    @Test
    @Order(7)
    void stopContainer_unsuccessful() throws Exception {
        DockerHandler stopHandler = new StopContainerHandler.Builder().withWaitTime(1).build();
        Promise<DockerResponseNode> promise = client.add(stopHandler).request();
        assertThrows(DockerResponseException.class, promise::sync);
    }

    @Test
    @Order(8)
    void removeContainer_unsuccessful() throws Exception {
        DockerHandler removeHandler = new RemoveContainerHandler.Builder().build();
        Promise<DockerResponseNode> promise = client.add(removeHandler).request();
        assertThrows(DockerResponseException.class, promise::sync);
    }

    @Test
    @Order(9)
    void chainRequest_successful() throws Exception {
        globalNode.clear();
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
        assertDoesNotThrow(promise::sync);
        assertEquals(4, globalNode.historySize());
        assertFalse(globalNode.getInternal("_container_id").isEmpty());
        DockerResponseNode localNode = promise.get();
        assertEquals(globalNode, localNode);
        assertFalse(localNode.toString().isEmpty());
    }

    @Test
    @Order(10)
    void chainRequest_unsuccessful_duplication() {
        DockerHandler createHandler = new CreateContainerHandler.Builder().build();
        assertThrows(DuplicationException.class, () -> client.add(createHandler).add(createHandler).request());
    }

    @Test
    @Order(11)
    void createContainer_fullConfig_successful() throws Exception {
        // TODO to perform this test, we need to implement InspectContainerHandler
        //  to check whether the configuration works and applied properly.
    }
}
