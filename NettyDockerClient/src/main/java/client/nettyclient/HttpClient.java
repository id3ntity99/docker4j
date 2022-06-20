package client.nettyclient;

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

    /**
     * Establish the connection between client and server(= Docker daemon).
     * {@link EventLoopGroup} contains one or more {@link io.netty.channel.EventLoop}, which is assigned to
     * each channel when the channel has been connected.
     * Each {@link io.netty.channel.EventLoop} contains {@link io.netty.channel.ChannelPipeline}, which contains one or more
     * {@link io.netty.channel.ChannelHandler}.
     * If there is any events to process in the channels, the EventLoops catch them and pass to the ChannelPipelines to process them as required.
     *
     * @param uri Target URI to connect.
     * @throws InterruptedException will be thrown when the connection process has been interrupted before completion.
     */
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

    /**
     * Create {@link Promise<SimpleResponse>} for later use.
     * The promise is used to get and return the response.
     *
     * @return {@link Promise<SimpleResponse>}.
     */
    private Promise<SimpleResponse> createPromise() {
        EventExecutor executor = channelFuture.channel()
                .pipeline()
                .context(HttpResponseHandler.class)
                .executor();
        // Create new promise for later use.
        return new DefaultPromise<>(executor);
    }

    /**
     * Initiate the HTTP request. If {@link io.netty.channel.ChannelInboundHandler} receives the response, it will write
     * the response to the {@link Promise}.
     *
     * @param targetURL A URI to send request.
     * @param req       A {@link FullHttpRequest} to be sent.
     * @return A {@link SimpleResponse} extracted from the {@link Promise}.
     * @throws InterruptedException occurs when sending request has been interrupted.
     * @throws ExecutionException   occurs when {@link Promise#get()}.
     */
    public SimpleResponse request(URI targetURL, FullHttpRequest req) throws InterruptedException, ExecutionException {
        Promise<SimpleResponse> promise = createPromise();
        // Set promise to the handler.
        channelFuture.channel().pipeline().get(HttpResponseHandler.class).setPromise(promise);
        // Send request.
        channelFuture.channel()
                .writeAndFlush(req)
                .addListener(new RequestFutureListener(targetURL.toString()));
        return promise.get();
    }
}
