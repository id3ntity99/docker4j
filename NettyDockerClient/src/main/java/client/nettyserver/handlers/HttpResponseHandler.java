package client.nettyserver.handlers;

import client.nettyserver.SimpleResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Promise;

public class HttpResponseHandler extends ChannelInboundHandlerAdapter {
    private Promise<SimpleResponse> promise;
    private final SimpleResponse simpleResponse = new SimpleResponse();

    /**
     * Read the data in the {@link io.netty.channel.Channel}.
     * This method uses {@link SimpleResponse#setBody(String)} and {@link SimpleResponse#setStatusCode(int)} for field initializations.
     * @param ctx A {@link ChannelHandlerContext} that enables the interaction between {@link io.netty.channel.ChannelHandler} and
     *            {@link io.netty.channel.ChannelPipeline}.
     * @param msg A message that this method reads.
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpResponse) {
            HttpResponse res = (HttpResponse) msg;
            simpleResponse.setStatusCode(res.status().code());
        } else if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;
            String stringBody = httpContent.content().toString(CharsetUtil.UTF_8);
            simpleResponse.setBody(stringBody);
        }
    }

    /**
     * Since the HTTP connection doesn't use Keep-alive option (that is, the HTTP Connection header's value is Closed),
     * the connection will be closed right after the requested objects are fully received.
     * If the connection is closed, the associated {@link io.netty.channel.Channel} becomes inactive.
     * As soon as the channel becomes inactive, the promise will be set to success with the {@link SimpleResponse}.
     * and the {@link io.netty.channel.EventLoop} assigned to the channel will be shutdown.
     * @param ctx A {@link ChannelHandlerContext} that enables the interaction between {@link io.netty.channel.ChannelHandler}
     *            and {@link io.netty.channel.ChannelPipeline}.
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        promise.setSuccess(simpleResponse);
        ctx.channel().eventLoop().shutdownGracefully();
    }

    /**
     * Set promise to the instance of this class.
     * Use {@link io.netty.channel.ChannelPipeline#get(Class)} to get this class instance to use this method.
     * For Example: <br/>
     * <code>channel.pipeline().get(HttpResponseHandler.class).setPromise(PROMISE)</code>
     * @param promise
     */
    public void setPromise(Promise<SimpleResponse> promise) {
        this.promise = promise;
    }
}
