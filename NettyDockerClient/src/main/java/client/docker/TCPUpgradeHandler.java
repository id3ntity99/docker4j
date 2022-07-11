package client.docker;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

public class TCPUpgradeHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpResponse res) throws Exception {
        if (res.status().code() == 101) {
            ctx.pipeline().remove(HttpClientCodec.class);
            ctx.pipeline().remove(HttpObjectAggregator.class);
            ctx.pipeline().remove(this);
            ctx.fireChannelActive();
        } else {
            System.out.println("Something went wrong");
        }
    }
}
