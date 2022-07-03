package client.docker.commands;

import client.docker.commands.exceptions.DockerRequestException;
import client.docker.configs.exec.ExecStartConfig;
import client.docker.dockerclient.NettyDockerClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.net.URI;
import java.net.URISyntaxException;

public class ExecStartCommand extends Command<ExecStartCommand, Void> {
    private final ExecStartConfig config = new ExecStartConfig();
    private String execId;
    private NettyDockerClient nettyDockerClient;

    @Override
    public ExecStartCommand withDockerClient(NettyDockerClient nettyDockerClient) {
        this.nettyDockerClient = nettyDockerClient;
        return this;
    }

    public ExecStartCommand withExecId(String execId) {
        this.execId = execId;
        return this;
    }

    public ExecStartCommand withDetach(boolean detach) {
        config.setDetach(detach);
        return this;
    }

    public ExecStartCommand withTty(boolean tty) {
        config.setTty(tty);
        return this;
    }

    @Override
    public Void exec() throws DockerRequestException {
        try {
            String stringUri = String.format("http://localhost:2375/exec/%s/start", execId);
            URI uri = new URI(stringUri);
            String body = writer.writeValueAsString(config);
            ByteBuf bodyBuffer = Unpooled.copiedBuffer(body, CharsetUtil.UTF_8);
            FullHttpRequest req = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.getRawPath());
            req.headers().set(HttpHeaderNames.HOST, uri.getHost());
            req.headers().set(HttpHeaderNames.UPGRADE, "tcp"); //이 헤더를 추가하면 HTTP가 TCP로 업그레이드, FrameDecoder 활성화됨.
            req.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE);//이 헤더를 추가하면 HTTP가 TCP로 업그레이드, FrameDecoder 활성화됨.
            req.headers().set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
            req.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
            req.content().writeBytes(bodyBuffer);
            req.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
            req.headers().set(HttpHeaderNames.CONTENT_LENGTH, bodyBuffer.readableBytes());
            nettyDockerClient.execute(req);
        } catch (JsonProcessingException | URISyntaxException e) {
            String errMsg = String.format("Exception raised while build command: %s", this.getClass().getSimpleName());
            throw new DockerRequestException(errMsg, e);
        }
        return null;
    }
}
