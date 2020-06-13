package container.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import org.springframework.stereotype.Component;

@Component
public class Client {
    private DockerClientConfig config;
    private DockerClient dockerClient;

    public Client() {
        this.config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://192.168.43.128:2375")
                .build();
        this.dockerClient =  DockerClientBuilder
                .getInstance(config)
                .build();
    }

    public DockerClientConfig getConfig() {
        return config;
    }

    public void setConfig(DockerClientConfig config) {
        this.config = config;
    }

    public DockerClient getDockerClient() {
        return dockerClient;
    }

    public void setDockerClient(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }
}
