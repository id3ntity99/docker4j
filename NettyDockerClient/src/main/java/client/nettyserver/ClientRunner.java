package client.nettyserver;

import client.nettyserver.handlers.CreateContainerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ClientRunner {
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("boss"));
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("worker"));

    public static void main(String[] args) throws Exception {
        InetSocketAddress bindAddress = new InetSocketAddress(InetAddress.getByName("localhost"), 65432);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.AUTO_READ, false)
                .group(bossGroup, workerGroup)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        System.out.println("Initializing channel...");
                        ch.pipeline().addLast(new HttpServerCodec());
                        ch.pipeline().addLast(new HttpObjectAggregator(9999));
                        ch.pipeline().addLast(new CreateContainerHandler());
                    }
                });
        // 로컬 포트에 채널 바인딩.
        bootstrap.bind(bindAddress).sync();
        System.out.println(1 & 0xff);
    }

}
