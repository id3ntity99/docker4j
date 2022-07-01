package client.nettyserver.handlers;

import client.docker.commands.Command;
import client.docker.commands.CreateContainerResponse;
import client.docker.dockerclient.proxy.ProxyDockerClient;
import client.nettyserver.SimpleResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleUserEventChannelHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

public class CreateContainerHandler extends SimpleUserEventChannelHandler<Command> {
    private ProxyDockerClient proxyDockerClient;
    private CreateContainerResponse createContainerResponse;

    public CreateContainerHandler(ProxyDockerClient proxyDockerClient) {
        this.proxyDockerClient = proxyDockerClient;
    }

    @Override
    public void eventReceived(ChannelHandlerContext ctx, Command cmd) throws Exception {
        proxyDockerClient.asyncRequest(cmd)
                .addListener(new FutureListener<SimpleResponse>() {
                    @Override
                    public void operationComplete(Future<SimpleResponse> future) throws Exception {
                        if (future.isSuccess()) {
                            System.out.println("Container Created..");
                            SimpleResponse res = future.get();
                            createContainerResponse = new ObjectMapper().readValue(res.getBody(), CreateContainerResponse.class);
                            String containerId = createContainerResponse.getId();
                            ctx.pipeline().replace(
                                    CreateContainerHandler.class,
                                    "startContainerHandler",
                                    new StartContainerHandler(proxyDockerClient)
                            );
                            ctx.fireUserEventTriggered(containerId);
                        }
                    }
                });
    }
}