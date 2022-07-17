package com.github.docker4j;

import com.github.docker4j.json.DockerResponseNode;
import com.github.docker4j.exceptions.DuplicationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Promise;

import java.io.Closeable;
import java.net.UnknownHostException;

public interface DockerClient extends Closeable {
    DockerClient add(DockerHandler handler);
    DockerClient withEventLoopGroup(EventLoopGroup eventLoopGroup);

    DockerClient withOutChannelClass(Class<? extends Channel> outChannelClass);

    DockerClient withAddress(String host, int port) throws UnknownHostException;

    ChannelFuture connect();

    Promise<DockerResponseNode> request() throws DuplicationException, JsonProcessingException;

    /**
     * Write a series of bytes into the interactive channel, which was established via {@link ExecStartHandler};
     *
     * @param in message to write.
     * @return {@link ChannelFuture} returned by {@link io.netty.channel.Channel#writeAndFlush(Object)} for the later use.
     */
    ChannelFuture write(ByteBuf in);

    /**
     *
     * Write a series of bytes into the interactive channel, which was established via {@link ExecStartHandler},
     * but this will not return {@link ChannelFuture} unlike {@link #write(ByteBuf)}.
     * That's why this method is called write-And-"Forget".
     *
     * @param in
     */
    void writeAndForget(ByteBuf in);
}
