package client.nettyClient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.ExecutionException;

public class HttpClient {
    private ChannelFuture channelFuture;
    private final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    public void connect(URI uri) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class)
                .group(group)
                .remoteAddress(uri.getHost(), uri.getPort())
                .handler(new HttpChannelInitializer());
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
        return new DefaultPromise<>(executor);
    }

    public String request(FullHttpRequest req) throws InterruptedException, ExecutionException {
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
