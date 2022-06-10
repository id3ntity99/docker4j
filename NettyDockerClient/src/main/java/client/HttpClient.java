package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultMaxBytesRecvByteBufAllocator;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class HttpClient {
    private ChannelFuture channelFuture;
    private EventLoopGroup group;
    private final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    public void connect(URI uri) throws InterruptedException {
        group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class)
                .group(group)
                .remoteAddress(uri.getHost(), uri.getPort())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.config().setRecvByteBufAllocator(new DefaultMaxBytesRecvByteBufAllocator(80900, 80900));
                        ch.pipeline().addLast(new HttpClientCodec());
                        ch.pipeline().addLast(new HttpClientResponseHandler());
                    }
                });
        channelFuture = bootstrap.connect().sync();
        if (channelFuture.isSuccess() && channelFuture.isDone()) {
            String loggerInfo = String.format("HTTP connection to %s is established", uri);
            logger.info(loggerInfo);
        }
    }

    public void request(FullHttpRequest req) {
        //TODO
        //  Request Builder 만들기
        String uri = req.uri();
        ChannelFuture writeFuture = channelFuture.channel().writeAndFlush(req);
        try {
            writeFuture.sync();
        } catch (InterruptedException e) {
            logger.debug("Thread stops while requesting", e);
            close();
        }
        if (writeFuture.isSuccess() && writeFuture.isDone()) {
            String loggerInfo = String.format("A request to %s is successfully done", uri);
            logger.info(loggerInfo);
        }
    }

    private void close() {
        channelFuture.channel().close();
        group.shutdownGracefully();
    }
}
