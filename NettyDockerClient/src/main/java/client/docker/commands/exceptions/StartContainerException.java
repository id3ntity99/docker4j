package client.docker.commands.exceptions;

public class StartContainerException extends RuntimeException{
    public StartContainerException(String message) {
        super(message);
    }
}
