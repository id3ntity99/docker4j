package client.nettyclient.initializers;

import client.nettyclient.handlers.HttpResponseHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultMaxBytesRecvByteBufAllocator;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;

/**
 * A {@link ChannelInitializer} for {@link io.netty.bootstrap.Bootstrap}.
 * The {@link io.netty.bootstrap.Bootstrap} uses this initializer to create channel handlers.
 */
public class HttpChannelInitializer extends ChannelInitializer<NioSocketChannel> {
    /**
     * Init channel.
     * This method created {@link DefaultMaxBytesRecvByteBufAllocator} to allocate {@link io.netty.buffer.ByteBuf} for receiving message.
     * @param ch            the {@link io.netty.channel.Channel} which was registered.
     */
    @Override
    public void initChannel(NioSocketChannel ch) {
        DefaultMaxBytesRecvByteBufAllocator byteBufAlloc = new DefaultMaxBytesRecvByteBufAllocator(8092, 8092);
        ch.config().setRecvByteBufAllocator(byteBufAlloc);
        ch.pipeline().addLast(new HttpClientCodec());
        ch.pipeline().addLast(new HttpResponseHandler());
    }
}
