package client.docker.dockerclient.exceptions;

public class DockerResponseException extends RuntimeException{
    public DockerResponseException(String message) {
        super(message);
    }
}
