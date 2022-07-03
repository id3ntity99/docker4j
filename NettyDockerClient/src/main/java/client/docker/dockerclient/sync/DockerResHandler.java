package client.docker.dockerclient.sync;

import client.nettyserver.SimpleResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Promise;

@Deprecated
public class DockerResHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    private Promise<SimpleResponse> promise;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpResponse res) throws Exception {
        if (res.status().code() >= 200 && res.status().code() <= 299) {
            SimpleResponse simpleResponse = new SimpleResponse();
            simpleResponse.setStatusCode(res.status().code());
            simpleResponse.setBody(res.content().toString(CharsetUtil.UTF_8));
            promise.setSuccess(simpleResponse);
        } else {
            throw new Exception("Request failed");
        }
    }

    public void setPromise(Promise<SimpleResponse> promise) {
        this.promise = promise;
    }
}
