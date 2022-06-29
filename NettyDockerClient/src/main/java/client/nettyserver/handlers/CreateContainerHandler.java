package client.nettyserver.handlers;

import client.docker.commands.CreateContainerCommand;
import client.docker.commands.CreateContainerResponse;
import client.docker.dockerclient.proxy.ProxyDockerClient;
import client.nettyserver.SimpleResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

public class CreateContainerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private ProxyDockerClient proxyDockerClient;
    private CreateContainerResponse createContainerResponse;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(this.getClass() + " active");
        Channel inboundChannel = ctx.channel();
        proxyDockerClient = new ProxyDockerClient().withInboundChannel(inboundChannel)
                .withOutChannelClass(ctx.channel().getClass())
                .withAddress("localhost", 2375)
                .withEventLoop(ctx.channel().eventLoop())
                .bootstrap();
        proxyDockerClient.connect();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        CreateContainerCommand createContainerCommand = new CreateContainerCommand().withImage("alpine")
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withTty(false)
                .withOpenStdin(true);
        proxyDockerClient.asyncRequest(createContainerCommand)
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
