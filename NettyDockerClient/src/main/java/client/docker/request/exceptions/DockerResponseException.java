package client.docker.request.exceptions;

public class DockerResponseException extends RuntimeException{
    public DockerResponseException(String message) {
        super(message);
    }
}
