package client.nettyserver.listeners;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

public class ConnectionListener implements ChannelFutureListener {
    private Channel inboundChannel;

    public ConnectionListener(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        if (future.isSuccess()) {
            inboundChannel.read();
        } else {
            future.cause().printStackTrace();
        }
    }
}
