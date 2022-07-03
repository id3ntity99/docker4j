package client.docker.dockerclient.handlers;

import client.docker.dockerclient.exceptions.DockerResponseException;
import client.nettyserver.SimpleResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Promise;

public class ProxyHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    private Promise<SimpleResponse> promise;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpResponse res) throws DockerResponseException {
        if (res.status().code() >= 200 && res.status().code() <= 299) {
            SimpleResponse simpleRes = new SimpleResponse();
            simpleRes.setStatusCode(res.status().code());
            simpleRes.setBody(res.content().toString(CharsetUtil.UTF_8));
            promise.setSuccess(simpleRes);
        } else {
            throw new DockerResponseException("Unsuccessful response: " + res.status().toString());
        }
    }

    public void setPromise(Promise<SimpleResponse> promise) {
        this.promise = promise;
    }
}
