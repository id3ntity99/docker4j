package client.docker;

import client.docker.exceptions.DockerRequestException;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DockerRequestHandler extends SimpleChannelInboundHandler<FullHttpResponse>{
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected DockerRequestHandler nextRequest = null;
    protected ByteBufAllocator allocator = new PooledByteBufAllocator();
    protected Promise<DockerResponseNode> promise = null;
    protected DockerResponseNode node = null;

    protected DockerRequestHandler(DockerRequestBuilder builder) {
    }

    protected DockerRequestHandler setPromise(Promise<DockerResponseNode> promise) {
        this.promise = promise;
        return this;
    }

    /**
     * Internal use only. Mostly, methods startng with "set" word are used by {@link RequestLinker}.
     * So, the methods are not exposed to the user.
     *
     * @param nextRequest
     * @return
     */
    protected DockerRequestHandler setNext(DockerRequestHandler nextRequest) {
        this.nextRequest = nextRequest;
        return this;
    }

    /**
     * Internal use only. Mostly, methods starting with "set" word are used by {@link RequestLinker}.
     * So, the methods are not exposed to the user.
     *
     * @param allocator
     * @return
     */
    protected DockerRequestHandler setAllocator(ByteBufAllocator allocator) {
        this.allocator = allocator;
        return this;
    }

    protected DockerRequestHandler setNode(DockerResponseNode node) {
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
