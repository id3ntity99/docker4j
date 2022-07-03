package client.docker.dockerclient.sync;

import client.docker.commands.Command;
import client.nettyserver.SimpleResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.util.concurrent.Promise;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

/**
 * @deprecated
 * Sync docker client.
 */
@Deprecated
public class DockerClient {
    protected EventLoopGroup eventLoop;
    protected InetSocketAddress dockerAddress;

    protected Bootstrap bootstrap;

    protected Channel outboundChannel;

    protected boolean isConnected = false;

    public DockerClient withEventLoop(EventLoopGroup eventLoop) {
        this.eventLoop = eventLoop;
        return this;
    }

    public DockerClient withAddress(String host, int port) throws UnknownHostException {
        this.dockerAddress = new InetSocketAddress(InetAddress.getByName(host), port);
        return this;
    }

    public DockerClient bootstrap() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class)
                .group(eventLoop)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new HttpClientCodec());
                        ch.pipeline().addLast(new HttpObjectAggregator(8092));
                        ch.pipeline().addLast(new DockerResHandler());
                    }
                });
        this.bootstrap = bootstrap;
        return this;
    }

    public void connect() throws InterruptedException{
        ChannelFuture future = bootstrap.connect(dockerAddress)
                .sync()
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws ConnectException {
                        if (future.isSuccess()) {
                            isConnected = true;
                        } else {
                            String errMsg = String.format("Connection failed: %s", future.cause().getMessage());
                            throw new ConnectException(errMsg);
                        }
                    }
                });
        this.outboundChannel = future.channel();
    }

    public SimpleResponse request(FullHttpRequest req) throws InterruptedException, ExecutionException {
        Promise<SimpleResponse> promise = outboundChannel.eventLoop().newPromise();
        outboundChannel.pipeline().get(DockerResHandler.class).setPromise(promise);
        outboundChannel.writeAndFlush(req).sync();
        return promise.get();
    }

    public boolean isConnected() {
        return isConnected;
    }
}
