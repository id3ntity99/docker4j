package client.nettyserver.handlers;

import client.docker.commands.ExecCreateResponse;
import client.docker.commands.ExecStartCommand;
import client.docker.dockerclient.proxy.ProxyDockerClient;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleUserEventChannelHandler;

public class ExecStartHandler extends SimpleUserEventChannelHandler<ExecCreateResponse> {
    private final ProxyDockerClient proxyDockerClient;

    public ExecStartHandler(ProxyDockerClient proxyDockerClient) {
        this.proxyDockerClient = proxyDockerClient;
    }

    @Override
    public void eventReceived(ChannelHandlerContext ctx, ExecCreateResponse execCreateResponse) {
        System.out.println("Starting exec...: " + execCreateResponse.getExecId());
        String execId = execCreateResponse.getExecId();
        ExecStartCommand execStartCommand = new ExecStartCommand(execId).withDetach(false)
                .withTty(true);
        proxyDockerClient.exec(execStartCommand).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    ctx.pipeline().remove(ExecStartHandler.class);
                }
            }
        });

    }
}
