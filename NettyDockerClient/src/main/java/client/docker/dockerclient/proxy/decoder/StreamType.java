package client.docker.dockerclient.proxy.decoder;

public enum StreamType {
    STDIN("stdin"),
    STDOUT("stdout"),
    STDERR("stderr"),
    RAW("raw");

    private String type;
    StreamType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
