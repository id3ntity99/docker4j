package client.docker;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Promise;

public interface DockerClient {
    ChannelFuture connect();

    Promise<Object> request() throws Exception;

    /**
     * Use this method if, and only if you added {@link ExecStartRequest} to the {@link RequestLinker} to interact with the created Docker container.
     * Invoking this method will establish an interactive session on the top of {@link io.netty.channel.Channel}, which represents the connection
     * to Docker on your machine.
     *
     * @throws Exception
     */
    void interact() throws Exception;

    /**
     * Write a series of bytes into the interactive channel, which was established by invoking {@link #interact()} method.
     *
     * @param in
     * @return {@link ChannelFuture} returned by {@link io.netty.channel.Channel#writeAndFlush(Object)} for the later use.
     */
    ChannelFuture write(ByteBuf in);

    /**
     * Write a series of bytes into the interactive channel, which was established by invoking {@link #interact()} method,
     * but this will not return {@link ChannelFuture} unlike {@link #write(ByteBuf)}.
     * That's why this method is called write-And-"Forget".
     *
     * @param in
     */
    void writeAndForget(ByteBuf in);
}
