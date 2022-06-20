package client.docker.commands.exceptions;

public class ContainerCreationException extends RuntimeException{
    public ContainerCreationException(String message) {
        super(message);
    }
}
