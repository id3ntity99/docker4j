package client.docker.request;

import client.docker.model.Container;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DockerRequest {
    protected static final ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    protected static final ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected DockerRequest nextRequest = null;
    protected Container container = new Container();
    protected ByteBufAllocator allocator = new PooledByteBufAllocator();

    protected DockerRequest(Builder builder) {
    }

    public abstract FullHttpRequest render() throws Exception;

    public abstract DockerResponseHandler handler();

    /**
     * Internal use only. Mostly, methods starting with "set" word are used by {@link RequestLinker}.
     * So, the methods are not exposed to the user.
     *
     * @param container
     * @return
     */
    protected abstract DockerRequest setContainer(Container container);

    /**
     * Internal use only. Mostly, methods startng with "set" word are used by {@link RequestLinker}.
     * So, the methods are not exposed to the user.
     *
     * @param nextRequest
     * @return
     */
    protected abstract DockerRequest setNextRequest(DockerRequest nextRequest);

    /**
     * Internal use only. Mostly, methods starting with "set" word are used by {@link RequestLinker}.
     * So, the methods are not exposed to the user.
     *
     * @param allocator
     * @return
     */
    protected abstract DockerRequest setAllocator(ByteBufAllocator allocator);

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    interface Builder {
        DockerRequest build();
    }

    public abstract static class DockerResponseHandler extends SimpleChannelInboundHandler<FullHttpResponse> {
        protected DockerRequest nextRequest = null;
        protected Container container;
        protected final Logger logger = LoggerFactory.getLogger(this.getClass());

        DockerResponseHandler(Container container, DockerRequest nextRequest) {
        }
    }
}
