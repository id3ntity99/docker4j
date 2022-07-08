package client.docker.request;

import client.docker.dockerclient.handlers.ProxyHandler;
import client.docker.model.Container;
import client.docker.model.SimpleResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.util.concurrent.Promise;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class NettyDockerClient implements DockerClient {
    private EventLoopGroup eventLoopGroup;
    private InetSocketAddress dockerAddress;
    private Bootstrap bootstrap;
    private Channel outboundChannel;
    private Channel inboundChannel;
    private Class<? extends Channel> outChannelClass;
    private RequestLinker aggregate;
    private List<DockerRequest> requestList = new ArrayList<>();

    public NettyDockerClient add(DockerRequest request) {
        requestList.add(request);
        return this;
    }

    public NettyDockerClient withEventLoopGroup(EventLoopGroup eventLoopGroup) {
        this.eventLoopGroup = eventLoopGroup;
        return this;
    }

    public NettyDockerClient withInboundChannel(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
        return this;
    }

    public NettyDockerClient withOutChannelClass(Class<? extends Channel> outChannelClass) {
        this.outChannelClass = outChannelClass;
        return this;
    }

    public NettyDockerClient withAddress(String host, int port) throws UnknownHostException {
        this.dockerAddress = new InetSocketAddress(InetAddress.getByName(host), port);
        return this;
    }

    public NettyDockerClient withAggregate(RequestLinker aggregate) {
        this.aggregate = aggregate;
        return this;
    }

    public Channel getOutboundChannel() {
        return this.outboundChannel;
    }

    public ByteBufAllocator getAllocator() {
        return outboundChannel.alloc();
    }

    public NettyDockerClient bootstrap() {
        bootstrap = new Bootstrap();
        bootstrap.channel(outChannelClass)
                .group(eventLoopGroup)
                .handler(new ResponseHandlerInit(aggregate));
        return this;
    }

    public ChannelFuture connect() {
        ChannelFuture future = bootstrap.connect(dockerAddress);
        this.outboundChannel = future.channel();
        return future;
    }

    @Deprecated
    public Promise<SimpleResponse> request(FullHttpRequest req) {
        Promise<SimpleResponse> promise = outboundChannel.eventLoop().newPromise();
        outboundChannel.pipeline().get(ProxyHandler.class).setPromise(promise);
        return promise;
    }

    public Promise<Container> request() throws Exception {
        DockerRequest firstRequest = aggregate.get(0);
        FullHttpRequest request = firstRequest.render();
        outboundChannel.writeAndFlush(request);
        return firstRequest.getPromise();
    }


    public ChannelFuture execute(FullHttpRequest req) {
        outboundChannel.pipeline().addLast(new TCPUpgradeHandler());
        outboundChannel.pipeline().addLast(new DockerFrameDecoder(inboundChannel));
        return outboundChannel.writeAndFlush(req);
    }

    private static class ResponseHandlerInit extends ChannelInitializer<SocketChannel> {
        private final RequestLinker linker;

        public ResponseHandlerInit(RequestLinker linker) {
            this.linker = linker;
        }

        private void configureLinker(SocketChannel ch) {
            Promise<Container> promise = ch.eventLoop().newPromise();
            linker.setAllocator(ch.alloc());
            linker.setPromise(promise);
        }

        @Override
        public void initChannel(SocketChannel ch) throws Exception {
            ch.config().setAllocator(new PooledByteBufAllocator(true));
            ch.pipeline().addLast(new HttpClientCodec());
            ch.pipeline().addLast(new HttpObjectAggregator(8092));
            configureLinker(ch);
            DockerRequest firstRequest = linker.link().get(0);
            ch.pipeline().addLast(firstRequest.handler());
        }
    }
}