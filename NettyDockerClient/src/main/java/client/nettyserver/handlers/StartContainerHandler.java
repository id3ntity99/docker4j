package client.nettyserver.handlers;

import client.docker.Container;
import client.docker.commands.StartContainerCommand;
import client.docker.dockerclient.NettyDockerClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleUserEventChannelHandler;

public class StartContainerHandler extends SimpleUserEventChannelHandler<Container> {
    private final NettyDockerClient nettyDockerClient;

    public StartContainerHandler(NettyDockerClient nettyDockerClient) {
        this.nettyDockerClient = nettyDockerClient;
    }

    @Override
    public void eventReceived(ChannelHandlerContext ctx, Container container) {
        new StartContainerCommand(container.getContainerId())
                .withDockerClient(nettyDockerClient)
                .exec();
        ctx.pipeline().replace(this, "execCreateHandler", new ExecCreateHandler(nettyDockerClient));
        ctx.fireUserEventTriggered(container);
    }
}
