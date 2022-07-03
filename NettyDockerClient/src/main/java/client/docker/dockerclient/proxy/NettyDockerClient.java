package client.docker.dockerclient.proxy;

import client.docker.commands.Command;
import client.docker.commands.exceptions.CommandBuildException;
import client.docker.dockerclient.proxy.decoder.DockerFrameDecoder;
import client.docker.dockerclient.proxy.exceptions.ProxyRequestException;
import client.docker.dockerclient.proxy.handlers.ProxyHandler;
import client.docker.dockerclient.proxy.handlers.TCPUpgradeHandler;
import client.nettyserver.SimpleResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
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

public class DockerClient {
    private EventLoopGroup eventLoop;
    private InetSocketAddress dockerAddress;

    private Bootstrap bootstrap;

    private Channel outboundChannel;

    private Channel inboundChannel;
    private Class<? extends Channel> outChannelClass;
    private Command command;

    public DockerClient withInboundChannel(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
        return this;
    }

    public DockerClient withOutChannelClass(Class<? extends Channel> outChannelClass) {
        this.outChannelClass = outChannelClass;
        return this;
    }

    public DockerClient withEventLoop(EventLoopGroup eventLoop) {
        this.eventLoop = eventLoop;
        return this;
    }

    public DockerClient withAddress(String host, int port) throws UnknownHostException {
        this.dockerAddress = new InetSocketAddress(InetAddress.getByName(host), port);
        return this;
    }

    public DockerClient bootstrap() {
        bootstrap = new Bootstrap();
        bootstrap.channel(outChannelClass)
                .group(new NioEventLoopGroup(4, new DefaultThreadFactory("dockerClient")))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new HttpClientCodec());
                        ch.pipeline().addLast(new HttpObjectAggregator(8092));
                        ch.pipeline().addLast(new ProxyHandler());
                    }
                });
        return this;
    }

    public ChannelFuture connect() {
        ChannelFuture future = bootstrap.connect(dockerAddress);
        this.outboundChannel = future.channel();
        return future;
    }

    public Channel getOutboundChannel() {
        return this.outboundChannel;
    }

    public Promise<SimpleResponse> request(FullHttpRequest req) {
        try {
            Promise<SimpleResponse> promise = outboundChannel.eventLoop().newPromise();
            outboundChannel.pipeline().get(ProxyHandler.class).setPromise(promise);
            outboundChannel.writeAndFlush(req);
            return promise;
        } catch (CommandBuildException e) {
            throw new ProxyRequestException("Exception raised while requesting", e);
        }
    }


    public ChannelFuture execute(FullHttpRequest req) {
        outboundChannel.pipeline().remove(ProxyHandler.class);
        outboundChannel.pipeline().addLast(new TCPUpgradeHandler());
        outboundChannel.pipeline().addLast(new DockerFrameDecoder(inboundChannel));
        return outboundChannel.writeAndFlush(req);
    }
}
