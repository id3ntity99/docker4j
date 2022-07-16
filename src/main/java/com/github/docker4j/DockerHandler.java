package com.github.docker4j;

import com.github.docker4j.exceptions.DockerRequestException;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DockerHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected DockerHandler nextRequest = null;
    protected ByteBufAllocator allocator = new PooledByteBufAllocator();
    protected Promise<DockerResponseNode> promise = null;
    protected DockerResponseNode node = null;

    protected DockerHandler(DockerRequestBuilder builder) {
    }

    protected DockerHandler setPromise(Promise<DockerResponseNode> promise) {
        this.promise = promise;
        return this;
    }

    protected DockerHandler setNext(DockerHandler nextRequest) {
        this.nextRequest = nextRequest;
        return this;
    }

    protected DockerHandler setAllocator(ByteBufAllocator allocator) {
        this.allocator = allocator;
        return this;
    }

    protected DockerHandler setNode(DockerResponseNode node) {
        this.node = node;
        return this;
    }

    protected void handOver() {
        nextRequest.setPromise(promise)
                .setAllocator(allocator)
                .setNode(node);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public abstract FullHttpRequest render() throws DockerRequestException;
}
