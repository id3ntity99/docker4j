package com.github.docker4j;

import com.github.docker4j.json.DockerResponseNode;
import com.github.docker4j.exceptions.DuplicationException;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class DefaultDockerClient implements DockerClient {
    private static final Logger logger = LoggerFactory.getLogger(DefaultDockerClient.class);
    private EventLoopGroup eventLoopGroup;
    private InetSocketAddress dockerAddress;
    private Channel outboundChannel;
    private Class<? extends Channel> outChannelClass;
    private DockerResponseNode node = new DockerResponseNode();
    private final List<DockerHandler> handlers = new ArrayList<>();

    private void checkDuplicates() throws DuplicationException {
        DockerHandler requestToCheck;
        int hashToCheck;
        for (int i = 0; i < handlers.size(); i++) {
            requestToCheck = handlers.get(i);
            hashToCheck = requestToCheck.hashCode();
            for (int j = 0; j < handlers.size(); j++) {
                if (j == i) {
                    continue;
                }
                if (hashToCheck == handlers.get(j).hashCode()) {
                    String err = String.format("Duplication detected: %s %s", requestToCheck.getClass(), handlers.get(j));
                    String message = "You need to create new instance of DockerRequest, even for the same operation." +
                            "Do not reuse the instances!";
                    throw new DuplicationException(err + message);
                }
            }
        }
        logger.debug("There are no duplications");
    }

    private void link(ByteBufAllocator allocator, Promise<DockerResponseNode> promise) throws DuplicationException {
        checkDuplicates();
        final int lastIndex = handlers.size() - 1;
        DockerHandler nextRequest;
        DockerHandler currentRequest;
        for (int i = 0; i <= handlers.size() - 1; i++) {
            currentRequest = handlers.get(i);
            if (i == 0) {
                currentRequest.setAllocator(allocator)
                        .setNode(node)
                        .setPromise(promise);
            }
            if (i != lastIndex) {
                nextRequest = handlers.get(i + 1);
                currentRequest.setNext(nextRequest);
                logger.debug("Linked: ({}, {}) =====> ({}, {})", currentRequest, currentRequest.hashCode(), nextRequest, nextRequest.hashCode());
            }
            handlers.set(i, currentRequest);
        }
        logger.debug("Completed request linking {}", handlers);
    }


    @Override
    public DefaultDockerClient add(DockerHandler handler) {
        handlers.add(handler);
        return this;
    }

    @Override
    public DefaultDockerClient withEventLoopGroup(EventLoopGroup eventLoopGroup) {
        this.eventLoopGroup = eventLoopGroup;
        return this;
    }

    @Override
    public DefaultDockerClient withOutChannelClass(Class<? extends Channel> outChannelClass) {
        this.outChannelClass = outChannelClass;
        return this;
    }

    @Override
    public DefaultDockerClient withAddress(String host, int port) throws UnknownHostException {
        this.dockerAddress = new InetSocketAddress(InetAddress.getByName(host), port);
        return this;
    }

    @Override
    public ChannelFuture connect() {
        ChannelFuture future = new Bootstrap().channel(outChannelClass)
                .group(eventLoopGroup)
                .handler(new DockerClientInit())
                .connect(dockerAddress);
        this.outboundChannel = future.channel();
        return future;
    }

    @Override
    public Promise<DockerResponseNode> request() throws DuplicationException {
        Promise<DockerResponseNode> resultPromise = outboundChannel.eventLoop().newPromise();
        link(outboundChannel.alloc(), resultPromise);
        DockerHandler firstHandler = handlers.get(0);
        outboundChannel.pipeline().addLast(firstHandler);
        FullHttpRequest request = firstHandler.render();
        outboundChannel.writeAndFlush(request);
        handlers.clear();
        logger.debug("Auto cleared {}", handlers);
        return resultPromise;
    }

    @Override
    public ChannelFuture write(ByteBuf in) {
        return outboundChannel.writeAndFlush(in);
    }

    @Override
    public void writeAndForget(ByteBuf in) {
        outboundChannel.writeAndFlush(in);
    }

    @Override
    public void close() {
        handlers.clear();
        eventLoopGroup.shutdownGracefully();
        outboundChannel.close();
    }

    private static class DockerClientInit extends ChannelInitializer<Channel> {
        @Override
        public void initChannel(Channel ch) {
            ch.config().setAllocator(new PooledByteBufAllocator(true));
            ch.pipeline().addLast(new HttpClientCodec());
            ch.pipeline().addLast(new HttpObjectAggregator(8092));
        }
    }
}
