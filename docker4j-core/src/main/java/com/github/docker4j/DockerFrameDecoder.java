package com.github.docker4j;

import com.github.docker4j.internal.stream.StreamType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketCloseStatus;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A subclass of the {@link ByteToMessageDecoder} to decode frames of the vnd.docker.raw-stream.
 *
 * @see <a href="https://docs.docker.com/engine/api/v1.41/#operation/ContainerAttach">Raw-stream</a>
 */
public class DockerFrameDecoder extends SimpleChannelInboundHandler<ByteBuf> {
    private final Channel inboundChannel;
    private static final  Logger LOGGER = LoggerFactory.getLogger(DockerFrameDecoder.class);

    public DockerFrameDecoder(Channel inboundChannel) {
        this.inboundChannel = inboundChannel;
    }

    private StreamType checkStreamType(byte header) {
        switch (header) {
            case 0:
                return StreamType.STDIN;
            case 1:
                return StreamType.STDOUT;
            case 2:
                return StreamType.STDERR;
            default:
                return StreamType.RAW;
        }
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        StreamType type = checkStreamType(in.getByte(0));
        if (!type.equals(StreamType.RAW)) {
            in.readBytes(8);
        }
        in.retain();
        WebSocketFrame wsFrame = new BinaryWebSocketFrame(in);
        inboundChannel.writeAndFlush(wsFrame).addListener(new WriteInboundListener());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.debug("DockerClient's channel is now inactive {}", ctx.channel());
    }

    private static class WriteInboundListener implements ChannelFutureListener{
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (!future.isSuccess()) {
                LOGGER.error("Exception raised while writing into inbound channel", future.cause());
                CloseWebSocketFrame closeFrame = new CloseWebSocketFrame(WebSocketCloseStatus.INTERNAL_SERVER_ERROR,
                        "Exception raised while writing to client");
                future.channel().writeAndFlush(closeFrame);
            }
        }
    }
}
