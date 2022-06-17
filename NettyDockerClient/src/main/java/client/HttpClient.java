package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.ExecutionException;

public class HttpClient {
    private ChannelFuture channelFuture;
    private EventLoopGroup group;
    private final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    public void connect(URI uri) throws InterruptedException {
        group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class)
                .group(group)
                .remoteAddress(uri.getHost(), uri.getPort())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) {
                        ch.config().setRecvByteBufAllocator(new DefaultMaxBytesRecvByteBufAllocator(80900, 80900));
                        ch.config().setAutoClose(true);
                        ch.pipeline().addLast(new HttpClientCodec());
                        ch.pipeline().addLast(new HttpResponseHandler());
                    }
                });
        channelFuture = bootstrap.connect().sync();
        if (channelFuture.isSuccess() && channelFuture.isDone()) {
            String loggerInfo = String.format("HTTP connection to %s is established", uri);
            logger.info(loggerInfo);
        }
    }

    private Promise<String> createPromise() {
        EventExecutor executor = channelFuture.channel()
                .pipeline()
                .context(HttpResponseHandler.class)
                .executor();
        // Create new promise for later use.
        Promise<String> promise = new DefaultPromise<>(executor);

        return promise;
    }

    public String request(FullHttpRequest req) throws InterruptedException, ExecutionException{
        String uri = req.uri();
        Promise<String> promise = createPromise();
        // Set promise to the handler.
        channelFuture.channel().pipeline().get(HttpResponseHandler.class).setPromise(promise);
        // Send request.
        try {
            channelFuture.channel()
                    .writeAndFlush(req)
                    .sync()
                    .addListener(new RequestFutureListener(uri));
        } catch (InterruptedException e) {
            logger.error("Exception Raised", e);
            Thread.currentThread().interrupt();
        }
        // Get response from the pre-created promise.
        return promise.get();
    }
}
