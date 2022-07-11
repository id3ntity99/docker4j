package client.docker;

import client.docker.exceptions.DuplicationException;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.util.concurrent.Promise;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class DefaultDockerClient implements DockerClient {
    private EventLoopGroup eventLoopGroup;
    private InetSocketAddress dockerAddress;
    private Channel outboundChannel;
    private Class<? extends Channel> outChannelClass;
    private RequestLinker linker;

    public DefaultDockerClient withEventLoopGroup(EventLoopGroup eventLoopGroup) {
        this.eventLoopGroup = eventLoopGroup;
        return this;
    }

    public DefaultDockerClient withOutChannelClass(Class<? extends Channel> outChannelClass) {
        this.outChannelClass = outChannelClass;
        return this;
    }

    public DefaultDockerClient withAddress(String host, int port) throws UnknownHostException {
        this.dockerAddress = new InetSocketAddress(InetAddress.getByName(host), port);
        return this;
    }

    public DefaultDockerClient withLinker(RequestLinker linker) {
        this.linker = linker;
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

    private DockerRequest configureLinker() throws DuplicationException {
        Promise<DockerResponseNode> promise1 = outboundChannel.eventLoop().newPromise();
        linker.setAllocator(outboundChannel.alloc());
        linker.setPromise(promise1);
        return linker.link().get(0);
    }

    public Promise<DockerResponseNode> request() throws DuplicationException {
        DockerRequest firstRequest = configureLinker();
        outboundChannel.pipeline().addLast(firstRequest.handler());
        FullHttpRequest request = firstRequest.render();
        outboundChannel.writeAndFlush(request);
        return firstRequest.getPromise();
    }


    @Override
    public void interact() throws DuplicationException {
        DockerRequest firstRequest = configureLinker();
        outboundChannel.pipeline().addLast(firstRequest.handler());
        FullHttpRequest request = firstRequest.render();
        outboundChannel.pipeline().addLast(new TCPUpgradeHandler());
        outboundChannel.writeAndFlush(request);
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
