package client.docker.dockerclient.proxy;

import client.docker.commands.Command;
import client.docker.commands.exceptions.CommandBuildException;
import client.docker.dockerclient.proxy.decoder.DockerFrameDecoder;
import client.docker.dockerclient.proxy.exceptions.ProxyRequestException;
import client.docker.dockerclient.proxy.handlers.ProxyHandler;
import client.docker.dockerclient.proxy.handlers.TCPUpgradeHandler;
import client.docker.dockerclient.sync.DockerClient;
import client.nettyserver.SimpleResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.util.concurrent.Promise;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class ProxyDockerClient extends DockerClient {
    private Channel inboundChannel;
    private Class<? extends Channel> outChannelClass;

    public ProxyDockerClient withInboundChannel(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
        return this;
    }

    public ProxyDockerClient withOutChannelClass(Class<? extends Channel> outChannelClass) {
        this.outChannelClass = outChannelClass;
        return this;
    }

    @Override
    public ProxyDockerClient withEventLoop(EventLoopGroup eventLoop) {
        this.eventLoop = eventLoop;
        return this;
    }

    @Override
    public ProxyDockerClient withAddress(String host, int port) throws UnknownHostException {
        this.dockerAddress = new InetSocketAddress(InetAddress.getByName(host), port);
        return this;
    }

    @Override
    public ProxyDockerClient bootstrap() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(outChannelClass)
                .group(eventLoop)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new HttpClientCodec());
                        ch.pipeline().addLast(new HttpObjectAggregator(8092));
                        ch.pipeline().addLast(new ProxyHandler());
                    }
                });
        this.bootstrap = bootstrap;
        return this;
    }

    public ChannelFuture asyncConnect() {
        ChannelFuture future = bootstrap.connect(dockerAddress);
        this.outboundChannel = future.channel();
        return future;
    }

    public Channel getOutboundChannel() {
        return this.outboundChannel;
    }

    public Promise<SimpleResponse> asyncRequest(Command command) {
        try {
            FullHttpRequest req = command.build();
            Promise<SimpleResponse> promise = outboundChannel.eventLoop().newPromise();
            outboundChannel.pipeline().get(ProxyHandler.class).setPromise(promise);
            outboundChannel.writeAndFlush(req);
            return promise;
        } catch (CommandBuildException e) {
            throw new ProxyRequestException("Exception raised while requesting", e);
        }
    }

    public ChannelFuture exec(Command execCommand) {
        FullHttpRequest req = execCommand.build();
        outboundChannel.pipeline().remove(ProxyHandler.class);
        outboundChannel.pipeline().addLast(new TCPUpgradeHandler());
        outboundChannel.pipeline().addLast(new DockerFrameDecoder(inboundChannel));
        return outboundChannel.writeAndFlush(req);
    }
}
