package client.nettyserver.handlers;

import client.docker.dockerclient.proxy.ProxyDockerClient;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;

public class WebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private ProxyDockerClient proxyDockerClient;
    private Channel outChannel;

    public WebSocketFrameHandler(ProxyDockerClient proxyDockerClient) {
        this.proxyDockerClient = proxyDockerClient;
        outChannel = proxyDockerClient.getOutboundChannel();
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Active websocket handler now");
        ctx.channel().read();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println("Inbound message: " + msg.text());
        ByteBuf buf = Unpooled.copiedBuffer(msg.text(), CharsetUtil.UTF_8);
        outChannel.writeAndFlush(buf).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    ctx.channel().read();
                } else {
                    future.cause().printStackTrace();
                }
            }
        });
    }
}
