package client.docker.commands.exceptions;

public class CommandBuildException extends RuntimeException{
    public CommandBuildException(String string, Throwable t) {
        super(string, t);
    }
}
