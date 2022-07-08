package client.docker;

import client.docker.model.Container;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.concurrent.Promise;

public interface DockerClient {
    DockerClient bootstrap();
    ChannelFuture connect();
    Promise<Container> request() throws Exception;
    ChannelFuture execute(FullHttpRequest req);
}
