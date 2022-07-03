package client.nettyserver.initializers;

import client.nettyserver.handlers.WebSocketInitHandler;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InChannelInitializer extends ChannelInitializer<SocketChannel> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public void initChannel(SocketChannel ch) {
        logger.info("Initializing inbound channel");
        ch.config().setAllocator(new PooledByteBufAllocator(true));
        ch.pipeline().addLast(new HttpServerCodec());
        ch.pipeline().addLast(new HttpObjectAggregator(9999));
        ch.pipeline().addLast(new WebSocketInitHandler());
    }
}
