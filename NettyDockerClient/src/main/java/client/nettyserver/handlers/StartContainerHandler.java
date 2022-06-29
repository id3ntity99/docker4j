package client.nettyserver.handlers;

import client.docker.commands.StartContainerCommand;
import client.docker.dockerclient.proxy.ProxyDockerClient;
import client.nettyserver.SimpleResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleUserEventChannelHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

public class StartContainerHandler extends SimpleUserEventChannelHandler<String> {
    private final ProxyDockerClient proxyDockerClient;

    public StartContainerHandler(ProxyDockerClient proxyDockerClient) {
        this.proxyDockerClient = proxyDockerClient;
    }

    @Override
    public void eventReceived(ChannelHandlerContext ctx, String containerId) {
        StartContainerCommand startCommand = new StartContainerCommand(containerId);
        proxyDockerClient.asyncRequest(startCommand)
                .addListener(new FutureListener<SimpleResponse>() {
                    @Override
                    public void operationComplete(Future<SimpleResponse> future) throws Exception {
                        if (future.isSuccess()) {
                            System.out.println("Container started..");
                            ctx.pipeline().replace(StartContainerHandler.class, "execCreateHandler", new ExecCreateHandler(proxyDockerClient));
                            ctx.fireUserEventTriggered(containerId);
                        }
                    }
                });
    }
}
