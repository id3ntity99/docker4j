package client.docker.dockerclient;

import client.nettyserver.SimpleResponse;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.concurrent.Promise;

public interface DockerClient {
    DockerClient bootstrap();
    ChannelFuture connect();
    Promise<SimpleResponse> request(FullHttpRequest req);
    ChannelFuture execute(FullHttpRequest req);
}
