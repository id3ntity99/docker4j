package client.docker.commands.exceptions;

@Deprecated
public class StartContainerException extends RuntimeException{
    public StartContainerException(String message) {
        super(message);
    }
}
