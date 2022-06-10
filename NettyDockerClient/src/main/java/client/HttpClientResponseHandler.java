package client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;
import org.json.JSONObject;

public class HttpClientResponseHandler extends SimpleChannelInboundHandler<Object> {
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpResponse) {
            HttpResponse res = (HttpResponse) msg;
            System.out.println(res.status());
        } else if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;
            String jsonString = httpContent.content().toString(CharsetUtil.UTF_8);
            JSONObject json = new JSONObject(jsonString);
            System.out.println(json);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.channel().close();
        ctx.channel().eventLoop().shutdownGracefully();
    }
}
