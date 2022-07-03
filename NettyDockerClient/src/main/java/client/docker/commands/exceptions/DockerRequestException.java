package client.docker.commands.exceptions;

public class CommandBuildException extends RuntimeException{
    public CommandBuildException(String message, Throwable t) {
        super(message, t);
    }

    public CommandBuildException(String message) {
        super(message);
    }
}
