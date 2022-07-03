package client.docker.commands.exceptions;

public class DockerRequestException extends RuntimeException{
    public DockerRequestException(String message, Throwable t) {
        super(message, t);
    }

    public DockerRequestException(String message) {
        super(message);
    }
}
