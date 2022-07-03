package client.docker.dockerclient.proxy.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.CharsetUtil;

//TODO FrameDecoder 구현하기.
//  1. Frame Header를 수신받으면 header를 제거.

/**
 * A subclass of the {@link ByteToMessageDecoder} to decode frames of the vnd.docker.raw-stream.
 *
 * @see <a href="https://docs.docker.com/engine/api/v1.41/#operation/ContainerAttach">Raw-stream</a>
 */
public class DockerFrameDecoder extends SimpleChannelInboundHandler<ByteBuf> {
    private final Channel inboundChannel;

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
    public void channelActive(ChannelHandlerContext ctx) {
        //inboundChannel.read();
        System.out.println("DockerFrameHandler Activated");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        StreamType type = checkStreamType(in.getByte(0));
        if (!type.equals(StreamType.RAW)) {
            in.readBytes(8);
        }
        BinaryWebSocketFrame wsFrame = new BinaryWebSocketFrame(in);
        wsFrame.retain(1);
        inboundChannel.writeAndFlush(wsFrame);
    }
}
