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

    private StreamType checkStreamType(byte[] headerBytes) {
        switch (headerBytes[0]) {
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

    private byte[] checkHeader(ByteBuf in) {
        byte[] headerBytes = new byte[8];
        in.getBytes(0, headerBytes, 0, 8);
        return headerBytes;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        inboundChannel.read();
        System.out.println("DockerFrameHandler Activated");
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        BinaryWebSocketFrame wsFrame = new BinaryWebSocketFrame(in);
        System.out.print("Exec content: ");
        System.out.println(in.toString(CharsetUtil.UTF_8));
        wsFrame.retain(1);
        inboundChannel.writeAndFlush(wsFrame);
    }
}
