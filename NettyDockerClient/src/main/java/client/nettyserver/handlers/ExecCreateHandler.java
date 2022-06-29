package client.nettyserver.handlers;

import client.docker.commands.ExecCreateCommand;
import client.docker.commands.ExecCreateResponse;
import client.docker.dockerclient.proxy.ProxyDockerClient;
import client.nettyserver.SimpleResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleUserEventChannelHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

public class ExecCreateHandler extends SimpleUserEventChannelHandler<String> {
    private final ProxyDockerClient proxyDockerClient;

    public ExecCreateHandler(ProxyDockerClient proxyDockerClient) {
        this.proxyDockerClient = proxyDockerClient;
    }

    @Override
    public void eventReceived(ChannelHandlerContext ctx, String containerId) {
        ExecCreateCommand execCreateCommand = new ExecCreateCommand(containerId).withTty(false)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withCmd(new String[]{"apk", "list"});

        proxyDockerClient.asyncRequest(execCreateCommand).addListener(new FutureListener<SimpleResponse>() {
            @Override
            public void operationComplete(Future<SimpleResponse> future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("Exec created...");
                    SimpleResponse res = future.get();
                    ExecCreateResponse execRes = new ObjectMapper().readValue(res.getBody(), ExecCreateResponse.class);
                    // ctx.pipeline().replace(ExecCreateHandler.class, "execStartHandler", new ExecStartHandler(proxyDockerClient));
                    // ctx.fireUserEventTriggered(execRes);
                }
            }
        });
    }
}
