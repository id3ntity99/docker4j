import client.docker.*;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RequestTest {
    @Test
    void createContainer_successful() throws Exception {
        DockerRequest request = new CreateContainerRequest.Builder().withImage("alpine").build();
        final RequestLinker linker = new RequestLinker(1);
        final DefaultDockerClient client = new DefaultDockerClient();
        linker.add(request);
        client.withAddress("localhost", 2375)
                .withEventLoopGroup(new NioEventLoopGroup())
                .withOutChannelClass(NioSocketChannel.class)
                .withLinker(linker)
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
        DockerRequest request = new CreateContainerRequest.Builder().withImage("#(!)*").build();
        final RequestLinker linker = new RequestLinker(1);
        final DefaultDockerClient client = new DefaultDockerClient();
        linker.add(request);

        client.withAddress("localhost", 2375)
                .withLinker(linker)
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
        DockerRequest createRequest = new CreateContainerRequest.Builder().withImage("alpine").build();
        DockerRequest startRequest = new StartContainerRequest.Builder().build();
        RequestLinker linker = new RequestLinker(2).add(createRequest).add(startRequest);
        DefaultDockerClient client = new DefaultDockerClient().withAddress("localhost", 2375)
                .withLinker(linker)
                .withOutChannelClass(NioSocketChannel.class)
                .withEventLoopGroup(new NioEventLoopGroup());
        client.connect().sync().addListener((ChannelFutureListener) future -> assertTrue(future.isSuccess()));
        DockerResponseNode responseNode = client.request().sync().get();
        assertTrue(responseNode.find("start_container_response").isEmpty());
        client.close();
    }

    @Test
    void startContainer_unsuccessful() throws Exception {
        DockerRequest createRequest = new CreateContainerRequest.Builder().withImage("alpine").withOpenStdin(true).build();
        DockerRequest startRequest1 = new StartContainerRequest.Builder().build();
        DockerRequest startRequest2 = new StartContainerRequest.Builder().build();
        RequestLinker linker = new RequestLinker(3).add(createRequest)
                .add(startRequest1)
                .add(startRequest2);
        DefaultDockerClient client = new DefaultDockerClient().withAddress("localhost", 2375)
                .withLinker(linker)
                .withOutChannelClass(NioSocketChannel.class)
                .withEventLoopGroup(new NioEventLoopGroup());
        client.connect().sync().addListener((ChannelFutureListener) future -> assertTrue(future.isSuccess()));
        DockerResponseNode responseNode = client.request().sync().get();
        System.out.println(responseNode.find("_error"));
        assertFalse(responseNode.find("_error").isEmpty());
        client.close();
    }
}
