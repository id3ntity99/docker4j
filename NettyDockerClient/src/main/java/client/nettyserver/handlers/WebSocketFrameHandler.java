package client.nettyserver.handlers;

import client.docker.dockerclient.NettyDockerClient;
import client.nettyserver.listeners.ListenAndReadListener;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

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
        ByteBuf inboundBytes = ctx.channel().alloc().heapBuffer().writeBytes(msg.content());
        outChannel.writeAndFlush(inboundBytes).addListener(new ListenAndReadListener(ctx.channel()));
    }
}
