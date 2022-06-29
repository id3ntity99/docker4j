package client.docker.dockerclient.proxy.exceptions;

public class ProxyRequestException extends RuntimeException{
    public ProxyRequestException(String message, Throwable t) {
        super(message, t);
    }
}
