package client.nettyserver.handlers;

import client.docker.commands.CreateContainerCommand;
import client.docker.dockerclient.proxy.ProxyDockerClient;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;

public class DockerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private ProxyDockerClient proxyDockerClient;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel inboundChannel = ctx.channel();
        proxyDockerClient = new ProxyDockerClient().withInboundChannel(inboundChannel)
                .withOutChannelClass(ctx.channel().getClass())
                .withAddress("localhost", 2375)
                .withEventLoop(ctx.channel().eventLoop())
                .bootstrap();
        proxyDockerClient.asyncConnect().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    CreateContainerCommand createContainerCommand = new CreateContainerCommand().withImage("alpine")
                            .withAttachStderr(true)
                            .withAttachStdin(true)
                            .withAttachStdout(true)
                            .withTty(false)
                            .withOpenStdin(true);
                    ctx.pipeline().addLast(new CreateContainerHandler(proxyDockerClient));
                    ctx.fireUserEventTriggered(createContainerCommand);
                }
            }
        });
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        System.out.println("Read invoked");
        System.out.println(ctx.pipeline());
    }
}
