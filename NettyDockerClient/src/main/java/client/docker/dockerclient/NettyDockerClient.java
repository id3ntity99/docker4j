package client.docker.dockerclient;

import client.docker.dockerclient.decoder.DockerFrameDecoder;
import client.docker.dockerclient.handlers.TCPUpgradeHandler;
import client.docker.dockerclient.handlers.ProxyHandler;
import client.nettyserver.SimpleResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.Promise;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class NettyDockerClient implements DockerClient {
    private EventLoopGroup eventLoop;
    private InetSocketAddress dockerAddress;

    private Bootstrap bootstrap;

    private Channel outboundChannel;

    private Channel inboundChannel;
    private Class<? extends Channel> outChannelClass;

    public NettyDockerClient withInboundChannel(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
        return this;
    }

    public NettyDockerClient withOutChannelClass(Class<? extends Channel> outChannelClass) {
        this.outChannelClass = outChannelClass;
        return this;
    }

    public NettyDockerClient withEventLoop(EventLoopGroup eventLoop) {
        this.eventLoop = eventLoop;
        return this;
    }

    public NettyDockerClient withAddress(String host, int port) throws UnknownHostException {
        this.dockerAddress = new InetSocketAddress(InetAddress.getByName(host), port);
        return this;
    }

    public Channel getOutboundChannel() {
        return this.outboundChannel;
    }

    public NettyDockerClient bootstrap() {
        bootstrap = new Bootstrap();
        bootstrap.channel(outChannelClass)
                .group(new NioEventLoopGroup(4, new DefaultThreadFactory("dockerClient")))
                .handler(new DockerChannelInitializer());
        return this;
    }

    public ChannelFuture connect() {
        ChannelFuture future = bootstrap.connect(dockerAddress);
        this.outboundChannel = future.channel();
        return future;
    }

    public Promise<SimpleResponse> request(FullHttpRequest req) {
        Promise<SimpleResponse> promise = outboundChannel.eventLoop().newPromise();
        outboundChannel.pipeline().get(ProxyHandler.class).setPromise(promise);
        outboundChannel.writeAndFlush(req);
        return promise;
    }


    public ChannelFuture execute(FullHttpRequest req) {
        outboundChannel.pipeline().remove(ProxyHandler.class);
        outboundChannel.pipeline().addLast(new TCPUpgradeHandler());
        outboundChannel.pipeline().addLast(new DockerFrameDecoder(inboundChannel));
        return outboundChannel.writeAndFlush(req);
    }

    private static class DockerChannelInitializer extends ChannelInitializer<SocketChannel>{
        @Override
        public void initChannel(SocketChannel ch) {
            ch.pipeline().addLast(new HttpClientCodec());
            ch.pipeline().addLast(new HttpObjectAggregator(8092));
            ch.pipeline().addLast(new ProxyHandler());
        }
    }
}
