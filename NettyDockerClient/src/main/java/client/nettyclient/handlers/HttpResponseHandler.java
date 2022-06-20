package client.nettyclient.handlers;

import client.nettyclient.response.SimpleResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Promise;

public class HttpResponseHandler extends ChannelInboundHandlerAdapter {
    private Promise<SimpleResponse> promise;
    private SimpleResponse simpleResponse = new SimpleResponse();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpResponse) {
            HttpResponse res = (HttpResponse) msg;
            simpleResponse.setStatusCode(res.status().code());
        } else if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;
            String stringBody = httpContent.content().toString(CharsetUtil.UTF_8);
            simpleResponse.setBody(stringBody);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        promise.setSuccess(simpleResponse);
        ctx.channel().eventLoop().shutdownGracefully();
    }

    public void setPromise(Promise<SimpleResponse> promise) {
        this.promise = promise;
    }
}
