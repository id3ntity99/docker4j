package client.nettyserver.handlers;

import client.docker.commands.CreateContainerCommand;
import client.docker.dockerclient.proxy.ProxyDockerClient;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketInitHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ProxyDockerClient proxyDockerClient;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelActive() called");
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
                            .withTty(true)
                            .withStdinOnce(false);
                    ctx.pipeline().addLast(new CreateContainerHandler(proxyDockerClient));
                    ctx.fireUserEventTriggered(createContainerCommand);
                }
            }
        });
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        boolean isUpgradeReq = req.headers().get(HttpHeaderNames.CONNECTION).equalsIgnoreCase("Upgrade");
        boolean isWsUpgradeReq = req.headers().get(HttpHeaderNames.UPGRADE).equalsIgnoreCase("WebSocket");
        if (isUpgradeReq && isWsUpgradeReq) {
            logger.info("Upgrading the connection...");
            handleHandshake(ctx, req).sync().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        ctx.pipeline().remove(WebSocketInitHandler.class);
                        ctx.pipeline().addLast(new WebSocketFrameHandler(proxyDockerClient));
                        ctx.pipeline().fireChannelActive();
                    }
                }
            });
        } else {
            logger.debug("Invalid request (Expected HTTP upgrade request)");
            HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
            ctx.writeAndFlush(res);
        }
    }

    private ChannelFuture handleHandshake(ChannelHandlerContext ctx, HttpRequest req) {
        logger.debug("Handshaking...");
        String hostHeader = req.headers().get("Host");
        String uriHeader = req.uri();
        String requestedUrl = "ws://" + hostHeader + uriHeader;
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(requestedUrl, null, true);
        WebSocketServerHandshaker handShaker = wsFactory.newHandshaker(req);
        return handShaker.handshake(ctx.channel(), req);
    }
}
