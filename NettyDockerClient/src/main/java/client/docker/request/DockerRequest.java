package client.docker.request;

import client.docker.model.Container;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DockerRequest {
    protected static final ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    protected static final ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected DockerRequest nextRequest = null;
    protected Container container = new Container();
    protected ByteBufAllocator allocator = new PooledByteBufAllocator();
    protected Promise<Container> promise;

    protected DockerRequest(DockerRequestBuilder builder) {
    }

    public DockerRequest setPromise(Promise<Container> promise) {
        this.promise = promise;
        return this;
    }

    public Promise<Container> getPromise() {
        return promise;
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

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public abstract FullHttpRequest render() throws Exception;

    public abstract DockerResponseHandler handler();
}
