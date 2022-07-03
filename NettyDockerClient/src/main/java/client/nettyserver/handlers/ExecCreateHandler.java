package client.nettyserver.handlers;

import client.docker.Container;
import client.docker.commands.ExecCreateCommand;
import client.docker.dockerclient.NettyDockerClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleUserEventChannelHandler;

@Deprecated
public class ExecCreateHandler extends SimpleUserEventChannelHandler<Container> {
    private final NettyDockerClient nettyDockerClient;

    public ExecCreateHandler(NettyDockerClient nettyDockerClient) {
        this.nettyDockerClient = nettyDockerClient;
    }

    @Override
    public void eventReceived(ChannelHandlerContext ctx, Container container) {
        String execId = new ExecCreateCommand().withDockerClient(nettyDockerClient)
                .withContainerId(container.getContainerId())
                .withTty(true)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withUser("root")
                .withCmd(new String[]{"/bin/sh"})
                .exec();
        ctx.pipeline().replace(this, "execStartHandler", new ExecStartHandler(nettyDockerClient));
        ctx.pipeline().fireUserEventTriggered(execId);
    }
}
