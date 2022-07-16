import client.docker.*;
import com.github.docker4j.*;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestTest {
    @Test
    void createContainer_successful() throws Exception {
        DockerHandler request = new CreateContainerHandler.Builder().withImage("alpine").build();
        final DefaultDockerClient client = new DefaultDockerClient();
        client.withAddress("localhost", 2375)
                .add(request)
                .withEventLoopGroup(new NioEventLoopGroup())
                .withOutChannelClass(NioSocketChannel.class)
                .connect()
                .sync() // Wait until the connection established.
                .addListener((ChannelFutureListener) future -> assertTrue(future.isSuccess()));
        DockerResponseNode node = client.request()
                .sync()
                .get();
        String containerId = node.find("_container_id");
        assertNotNull(containerId);
        client.close();
    }

    @Test
    void createContainer_unsuccessful() throws Exception {
        DockerHandler request = new CreateContainerHandler.Builder().withImage("#(!)*").build();
        final DefaultDockerClient client = new DefaultDockerClient();

        client.withAddress("localhost", 2375)
                .add(request)
                .withOutChannelClass(NioSocketChannel.class)
                .withEventLoopGroup(new NioEventLoopGroup())
                .connect()
                .sync()
                .addListener((ChannelFutureListener) future -> assertTrue(future.isSuccess()));

        DockerResponseNode responseNode = client.request().sync().get();
        assertFalse(responseNode.find("_error").isEmpty());
        client.close();
    }

    @Test
    void startContainer_successful() throws Exception {
        DockerHandler createRequest = new CreateContainerHandler.Builder().withImage("alpine").build();
        DockerHandler startRequest = new StartContainerHandler.Builder().build();
        DefaultDockerClient client = new DefaultDockerClient().withAddress("localhost", 2375)
                .add(createRequest)
                .add(startRequest)
                .withOutChannelClass(NioSocketChannel.class)
                .withEventLoopGroup(new NioEventLoopGroup());
        client.connect().sync().addListener((ChannelFutureListener) future -> assertTrue(future.isSuccess()));
        DockerResponseNode responseNode = client.request().sync().get();
        assertTrue(responseNode.find("start_container_response").isEmpty());
        client.close();
    }

    @Test
    void startContainer_unsuccessful() throws Exception {
        DockerHandler createRequest = new CreateContainerHandler.Builder().withImage("alpine").withOpenStdin(true).build();
        DockerHandler startRequest1 = new StartContainerHandler.Builder().build();
        DockerHandler startRequest2 = new StartContainerHandler.Builder().build();
        DefaultDockerClient client = new DefaultDockerClient().withAddress("localhost", 2375)
                .add(createRequest)
                .add(startRequest1)
                .add(startRequest2)
                .withOutChannelClass(NioSocketChannel.class)
                .withEventLoopGroup(new NioEventLoopGroup());
        client.connect().sync().addListener((ChannelFutureListener) future -> assertTrue(future.isSuccess()));
        DockerResponseNode responseNode = client.request().sync().get();
        System.out.println(responseNode.find("_error"));
        assertFalse(responseNode.find("_error").isEmpty());
        client.close();
    }
}
