package com.github.docker4j;

import com.github.docker4j.json.DockerResponseNode;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
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
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(1);
    private InetSocketAddress dockerAddress;
    private Channel outboundChannel;
    private Class<? extends Channel> outChannelClass;
    private final List<DockerHandler> handlers = new ArrayList<>();

    private void link(ByteBufAllocator allocator, Promise<DockerResponseNode> promise) {
        DockerHandler current;
        for (int i = 0; i < handlers.size(); i++) {
            current = handlers.get(i);
            current.setPromise(promise);
            current.setAllocator(allocator);
            if (i == handlers.size() - 1)
                outboundChannel.pipeline().addLast("last", current);
            else
                outboundChannel.pipeline().addLast(current);
        }
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
        logger.info("Connecting to Dockerd");
        ChannelFuture future = new Bootstrap().channel(outChannelClass)
                .group(eventLoopGroup)
                .handler(new DockerClientInit())
                .connect(dockerAddress);
        this.outboundChannel = future.channel();
        return future;
    }

    @Override
    public Promise<DockerResponseNode> request() {
        DockerResponseNode node = new DockerResponseNode();
        logger.debug("Requesting {}", handlers);
        Promise<DockerResponseNode> resultPromise = outboundChannel.eventLoop().newPromise();
        link(outboundChannel.alloc(), resultPromise);
        outboundChannel.pipeline().fireUserEventTriggered(node);
        handlers.clear();
        logger.debug("Auto cleared {}", handlers);
        return resultPromise;
    }

    @Override
    public ChannelFuture write(ByteBuf in) {
        return outboundChannel.writeAndFlush(in).addListener(new WriteListener());
    }

    @Override
    public void writeAndForget(ByteBuf in) {
        outboundChannel.writeAndFlush(in);
    }

    @Override
    public void close() {
        handlers.clear();
        outboundChannel.disconnect();
        outboundChannel.pipeline().close();
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

    private static class WriteListener implements ChannelFutureListener {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (!future.isSuccess()) {
                logger.error("DockerClient#write(ByteBuf in) raised an exception while writing into outbound channel", future.cause());
            }
        }
    }
}
