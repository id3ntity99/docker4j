package client.docker.commands.exceptions;

@Deprecated
public class ContainerCreationException extends RuntimeException{
    public ContainerCreationException(String message) {
        super(message);
    }
}
