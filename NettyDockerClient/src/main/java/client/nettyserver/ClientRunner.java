package client.nettyserver;

import client.nettyserver.initializers.InChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public class ClientRunner {
    private static final EventLoopGroup bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("boss"));
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("worker"));

    //TODO
    //  1. 1차 기능구현(웹소켓 클라이언트-서버 연결 및 도커 프록싱) 성공.
    //  2. 코드 리팩터링
    //  3. 보류했던 기능들 추가 (e.g. docker api)
    //  3. 테스트코드 작성
    //  4. 퍼포먼스 테스트 및 로드 테스트
    public static void main(String[] args) throws Exception {
        InetSocketAddress bindAddress = new InetSocketAddress(InetAddress.getByName("localhost"), 1111);
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.AUTO_READ, false)
                .group(bossGroup, workerGroup)
                .childHandler(new InChannelInitializer());
        // 로컬 포트에 채널 바인딩.
        bootstrap.bind(bindAddress).sync();
    }

}
