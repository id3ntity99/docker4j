package client.docker.dockerclient.proxy.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

//TODO FrameDecoder 구현하기.
public class DockerFrameDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println(ctx.pipeline());
        System.out.println(in.getByte(0));
    }
}
