package client.docker.request;

import client.docker.model.Container;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DockerRequest {
    protected static final ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    protected static final ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected DockerRequest nextRequest = null;
    protected Container container;
    protected ByteBufAllocator allocator = new PooledByteBufAllocator();
    protected Promise<Container> promise;

    protected DockerRequest(DockerRequestBuilder builder) {
    }

    protected DockerRequest setPromise(Promise<Container> promise) {
        this.promise = promise;
        return this;
    }

    /**
     * Internal use only. Mostly, methods starting with "set" word are used by {@link RequestLinker}.
     * So, the methods are not exposed to the user.
     *
     * @param container
     * @return
     */
    protected DockerRequest setContainer(Container container) {
        this.container = container;
        return this;
    }

    /**
     * Internal use only. Mostly, methods startng with "set" word are used by {@link RequestLinker}.
     * So, the methods are not exposed to the user.
     *
     * @param nextRequest
     * @return
     */
    protected DockerRequest setNext(DockerRequest nextRequest) {
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
    protected DockerRequest setAllocator(ByteBufAllocator allocator) {
        this.allocator = allocator;
        return this;
    }

    protected Promise<Container> getPromise() {
        return promise;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public abstract FullHttpRequest render() throws Exception;

    /**
     * Instantiates internal-use only channel inbound handler.
     * the reason of invoking this method is to create new instance of the subclasses of {@link ChannelInboundHandlerAdapter},
     * such as {@link io.netty.channel.SimpleChannelInboundHandler} and its subclass {@link DockerResponseHandler}.
     * For the most of the time, the return value of this method is used for the simplest task:
     * <strong>add the handler to the {@link io.netty.channel.ChannelPipeline}</strong>
     * @return
     */
    protected abstract ChannelInboundHandlerAdapter handler();
}
