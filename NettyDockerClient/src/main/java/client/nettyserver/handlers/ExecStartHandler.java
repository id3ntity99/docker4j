package client.nettyserver.handlers;

import client.docker.commands.ExecStartCommand;
import client.docker.dockerclient.proxy.NettyDockerClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleUserEventChannelHandler;

public class ExecStartHandler extends SimpleUserEventChannelHandler<String> {
    private final NettyDockerClient nettyDockerClient;

    public ExecStartHandler(NettyDockerClient nettyDockerClient) {
        this.nettyDockerClient = nettyDockerClient;
    }

    @Override
    public void eventReceived(ChannelHandlerContext ctx, String execId) {
        new ExecStartCommand(execId).withDetach(false)
                .withDockerClient(nettyDockerClient)
                .withTty(true)
                .exec();
        ctx.pipeline().remove(ExecStartHandler.class);

    }
}
