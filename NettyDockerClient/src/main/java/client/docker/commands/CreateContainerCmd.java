package client.docker.commands;

import client.docker.commands.exceptions.ContainerCreationException;
import client.docker.commands.uris.URIs;
import client.json.configs.config.Config;
import client.json.configs.config.ExposedPorts;
import client.json.configs.config.HealthConfig;
import client.json.configs.config.Volumes;
import client.json.configs.hostconfig.HostConfig;
import client.json.configs.hostconfig.networkingconfig.NetworkingConfig;
import client.nettyclient.HttpClient;
import client.nettyclient.response.SimpleResponse;
import client.request.POSTRequest;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.netty.handler.codec.http.FullHttpRequest;

import java.net.URI;
import java.util.Map;

/**
 * Create config and request Docker daemon for creating a container.
 * Since this class creates {@link Config}, user doesn't have to create {@link Config} separately.
 * All the user has to do is just using the methods of this class to configure the {@link Config},
 * and invoke {@link CreateContainerCmd#exec()} to make a request.
 */
public class CreateContainerCmd extends AbstractCommand<CreateContainerResponse>{
    private final HttpClient httpClient = new HttpClient();
    private final Config config = new Config();
    private final ObjectMapper mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    private final ObjectWriter writer = mapper.writer().withDefaultPrettyPrinter();
    private final URI targetUri = URIs.CREATE_CONTAINER.uri();

    public CreateContainerCmd withHostConfig(HostConfig hostConfig) {
        config.setHostConfig(hostConfig);
        return this;
    }

    public CreateContainerCmd withNetworkingConfig(NetworkingConfig networkingConfig) {
        config.setNetworkingConfig(networkingConfig);
        return this;
    }

    public CreateContainerCmd withHostname(String hostname) {
        config.setHostName(hostname);
        return this;
    }

    public CreateContainerCmd withDomainName(String domainName) {
        config.setDomainName(domainName);
        return this;
    }

    public CreateContainerCmd withUser(String user) {
        config.setUser(user);
        return this;
    }

    public CreateContainerCmd withAttachStdin(boolean attachStdin) {
        config.setAttachStdin(attachStdin);
        return this;
    }

    public CreateContainerCmd withAttachStdout(boolean attachStdout) {
        config.setAttachStdout(attachStdout);
        return this;
    }

    public CreateContainerCmd withAttachStderr(boolean attachStderr) {
        config.setAttachStderr(attachStderr);
        return this;
    }

    public CreateContainerCmd withTty(boolean tty) {
        config.setTty(tty);
        return this;
    }

    public CreateContainerCmd withOpenStdin(boolean openStdin) {
        config.setOpenStdin(openStdin);
        return this;
    }

    public CreateContainerCmd withStdinOnce(boolean stdinOnce) {
        config.setStdinOnce(stdinOnce);
        return this;
    }

    public CreateContainerCmd withExposedPorts(ExposedPorts exposedPorts) {
        config.setExposedPorts(exposedPorts);
        return this;
    }

    public CreateContainerCmd withEnv(String[] env) {
        config.setEnv(env);
        return this;
    }

    public CreateContainerCmd withCmd(String[] cmd) {
        config.setCmd(cmd);
        return this;
    }

    public CreateContainerCmd withHealthConfig(HealthConfig healthConfig) {
        config.setHealthConfig(healthConfig);
        return this;
    }

    public CreateContainerCmd withArgsEscaped(boolean argsEscaped) {
        config.setArgsEscaped(argsEscaped);
        return this;
    }

    public CreateContainerCmd withImage(String image) {
        config.setImage(image);
        return this;
    }

    public CreateContainerCmd withVolumes(Volumes volumes) {
        config.setVolumes(volumes);
        return this;
    }

    public CreateContainerCmd withWorkingDir(String workingDir) {
        config.setWorkingDir(workingDir);
        return this;
    }

    public CreateContainerCmd withEntryPoint(String[] entryPoint) {
        config.setEntryPoint(entryPoint);
        return this;
    }

    public CreateContainerCmd withNetworkDisabled(boolean networkDisabled) {
        config.setNetworkDisabled(networkDisabled);
        return this;
    }

    public CreateContainerCmd withMacAddress(String macAddress) {
        config.setMacAddress(macAddress);
        return this;
    }

    public CreateContainerCmd withOnBuild(String[] onBuild) {
        config.setOnBuild(onBuild);
        return this;
    }

    public CreateContainerCmd withLabels(Map<String, String> labels) {
        config.setLabels(labels);
        return this;
    }

    public CreateContainerCmd withStopSignal(String stopSignal) {
        config.setStopSignal(stopSignal);
        return this;
    }

    public CreateContainerCmd withStopTimeout(int stopTimeout) {
        config.setStopTimeout(stopTimeout);
        return this;
    }

    public CreateContainerCmd withShell(String[] shell) {
        config.setShell(shell);
        return this;
    }

    public CreateContainerResponse exec() throws Exception {
        String jsonString = writer.writeValueAsString(config);
        FullHttpRequest req = new POSTRequest().withURI(targetUri)
                .withBody(jsonString)
                .build();
        SimpleResponse simpleRes = httpClient.request(URIs.CREATE_CONTAINER.uri(), req);
        if (simpleRes.getStatusCode() != 201) {
            String errMsg = String.format(
                    "[%s] Cannot create container due to: %s",
                    simpleRes.getStatusCode(),
                    simpleRes.getBody()
            );
            throw new ContainerCreationException(errMsg);
        }
        String info = String.format("Container created: %s", simpleRes.getBody());
        logger.info(info);
        return mapper.readValue(simpleRes.getBody(), CreateContainerResponse.class);
    }
}
