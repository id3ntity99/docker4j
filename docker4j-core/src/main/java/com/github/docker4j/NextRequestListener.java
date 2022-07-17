package docker4j;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NextRequestListener implements ChannelFutureListener {
    private final ChannelHandlerContext ctx;
    private final DockerHandler currentHandler;
    private final DockerHandler nextRequest;

    public NextRequestListener(ChannelHandlerContext ctx, DockerHandler currentHandler, DockerHandler nextRequest) {
        this.ctx = ctx;
        this.currentHandler = currentHandler;
        this.nextRequest = nextRequest;
    }

    @Override
    public void operationComplete(ChannelFuture future) throws Exception {
        if (future.isSuccess()) {
            ChannelInboundHandlerAdapter nextHandler = nextRequest;
            ctx.pipeline().replace(currentHandler.getClass(), nextHandler.toString(), nextHandler);
        } else {
            String errMsg = String.format("Exception raised while sending next %s", nextRequest.getClass().getSimpleName());
        }
    }
}
