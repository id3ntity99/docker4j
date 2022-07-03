package client.nettyserver.handlers;

import client.docker.Container;
import client.docker.commands.CreateContainerCommand;
import client.docker.dockerclient.proxy.NettyDockerClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleUserEventChannelHandler;

public class CreateContainerHandler extends SimpleUserEventChannelHandler<Boolean> {
    private final NettyDockerClient nettyDockerClient;

    public CreateContainerHandler(NettyDockerClient nettyDockerClient) {
        this.nettyDockerClient = nettyDockerClient;
    }

    @Override
    public void eventReceived(ChannelHandlerContext ctx, Boolean evt) throws Exception {
        System.out.println("Requesting create container");
        Container container = new CreateContainerCommand().withDockerClient(nettyDockerClient)
                .withImage("alpine")
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withTty(true)
                .withStdinOnce(false)
                .exec();
        ctx.pipeline().replace(this, "startContainerHandler", new StartContainerHandler(nettyDockerClient));
        ctx.fireUserEventTriggered(container);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //TODO Implement this exception handler.
    }
}