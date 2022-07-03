package client.nettyserver.handlers;

import client.docker.dockerclient.proxy.NettyDockerClient;
import client.nettyserver.listeners.ListenAndReadListener;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;

public class WebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final Channel outChannel;

    public WebSocketFrameHandler(NettyDockerClient proxyNettyDockerClient) {
        outChannel = proxyNettyDockerClient.getOutboundChannel();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println("Active websocket handler now");
        ctx.channel().read();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        ByteBuf buf = Unpooled.copiedBuffer(msg.text(), CharsetUtil.UTF_8);
        outChannel.writeAndFlush(buf).addListener(new ListenAndReadListener(ctx.channel()));
    }
}
