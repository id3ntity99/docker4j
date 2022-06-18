package client.nettyclient;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;

public class HttpChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    public void initChannel(SocketChannel ch) {
        //ch.config().setRecvByteBufAllocator(new DefaultMaxBytesRecvByteBufAllocator(80900, 80900));
        ch.pipeline().addLast(new HttpClientCodec());
        ch.pipeline().addLast(new HttpResponseHandler());
    }
}
