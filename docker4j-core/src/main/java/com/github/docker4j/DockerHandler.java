package com.github.docker4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.docker4j.exceptions.DockerRequestException;
import com.github.docker4j.json.DockerResponseNode;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.channel.ChannelHandler.Sharable;

@Sharable
public abstract class DockerHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final ObjectMapper mapper = new ObjectMapper();
    protected ByteBufAllocator allocator = new PooledByteBufAllocator();
    protected Promise<DockerResponseNode> promise = null;
    protected DockerResponseNode node = null;

    protected DockerHandler(DockerRequestBuilder builder) {
    }

    protected DockerHandler setPromise(Promise<DockerResponseNode> promise) {
        this.promise = promise;
        return this;
    }

    protected DockerHandler setAllocator(ByteBufAllocator allocator) {
        this.allocator = allocator;
        return this;
    }

    protected abstract DockerResponse parseResponseBody(String responseBody) throws JsonProcessingException;

    protected void checkLast(ChannelHandlerContext ctx, DockerResponse response) {
        node.add(response);
        if (ctx.name().equals("last"))
            promise.setSuccess(node);
        ctx.pipeline().remove(this);
        ctx.pipeline().fireUserEventTriggered(node);
    }

    protected void checkLast(ChannelHandlerContext ctx) {
        if (ctx.name().equals("last"))
            promise.setSuccess(node);
        ctx.pipeline().remove(this);
        ctx.pipeline().fireUserEventTriggered(node);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable t) {
        promise.setFailure(t);
        ctx.pipeline().remove(this);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public abstract FullHttpRequest render() throws DockerRequestException;
}
