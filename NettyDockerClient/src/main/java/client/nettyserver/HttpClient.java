package client.nettyserver;

import client.docker.dockerclient.sync.DockerClient;
import client.nettyserver.handlers.HttpResponseHandler;
import client.nettyserver.initializers.HttpChannelInitializer;
import client.nettyserver.listeners.RequestFutureListener;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.concurrent.ExecutionException;

/**
 * @deprecated Use {@link DockerClient} instead.
 */
@Deprecated
public class HttpClient {
    private final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    /**
     * Establish the connection between client and server(= Docker daemon).
     * {@link EventLoopGroup} contains one or more {@link io.netty.channel.EventLoop}, which is assigned to
     * each channel when the channel has been connected.
     * Each {@link io.netty.channel.EventLoop} contains {@link io.netty.channel.ChannelPipeline}, which contains one or more
     * {@link io.netty.channel.ChannelHandler}.
     * If there is any events to process in the channels, the EventLoops catch and pass them to the ChannelPipelines to process them as required.
     * Note that, the connection process will be blocking.
     *
     * @param uri Target URI to connect.
     * @throws InterruptedException will be thrown when the connection process has been interrupted before completion.
     */
    private Channel connect(URI uri) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class)
                .group(group)
                .remoteAddress(uri.getHost(), uri.getPort())
                .handler(new HttpChannelInitializer());
        ChannelFuture channelFuture = bootstrap.connect().sync();
        if (channelFuture.isSuccess() && channelFuture.isDone()) {
            String loggerInfo = String.format("HTTP connection to %s is established", uri);
            logger.info(loggerInfo);
        }
        return channelFuture.channel();
    }


    /**
     * Initiate the asynchronous HTTP request.
     * If this method is invoked, it will automatically connect to the remote address, create the {@link EventLoopGroup},
     * establish the connection(the channel).
     * After sending the request, the {@link HttpResponseHandler} will receive the Http response, and set the promise to success.
     * As soon as the promise has been set to success, its result will be returned.
     * Even if this method makes use of asynchronous request, it will block until the promise is marked as success with the result.
     *
     * @param uri A URI to send request.
     * @param req A {@link FullHttpRequest} to be sent.
     * @return A {@link SimpleResponse} extracted from the {@link Promise}.
     * @throws InterruptedException occurs when sending request has been interrupted.
     * @throws ExecutionException   occurs when {@link Promise#get()}.
     */
    public SimpleResponse request(URI uri, FullHttpRequest req) throws InterruptedException, ExecutionException {
        Channel channel = connect(uri);
        Promise<SimpleResponse> promise = channel.eventLoop().newPromise();
        channel.pipeline()
                .get(HttpResponseHandler.class)
                .setPromise(promise);
        channel.writeAndFlush(req).addListener(new RequestFutureListener(uri));
        return promise.get();
    }
}
