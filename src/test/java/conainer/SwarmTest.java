package conainer;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.ServiceSpec;
import com.github.dockerjava.api.model.SwarmSpec;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.google.gson.Gson;

public class SwarmTest {

    public static void main(String[] args) {
        Gson gson = new Gson();
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://192.168.43.128:2375")
                .build();

        DockerClient dockerClient = DockerClientBuilder
                .getInstance(config)
                .build();
        dockerClient.createServiceCmd(new ServiceSpec()

        ).exec();

    }
}
