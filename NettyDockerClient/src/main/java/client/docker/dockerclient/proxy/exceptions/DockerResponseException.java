package client.docker.dockerclient.proxy.exceptions;

public class DockerResponseException extends RuntimeException{
    public DockerResponseException(String message) {
        super(message);
    }
}
