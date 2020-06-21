package conainer;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerConfig;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ContainerTest {

    public static void main(String[] args) throws URISyntaxException, InterruptedException, IOException {
        Gson gson = new Gson();
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://192.168.43.128:2375")
                .build();

        DockerClient dockerClient = DockerClientBuilder
                .getInstance(config)
                .build();

        ExposedPort[] exposedPorts = dockerClient.inspectImageCmd("014516db4bff").exec().getConfig().getExposedPorts();
        System.out.println(gson.toJson(exposedPorts));
    }

    private static void createContainer2(Gson gson, DockerClient dockerClient) {
        CreateContainerCmd nginx = dockerClient.createContainerCmd("nginx");

        ExposedPort tcp80 = ExposedPort.tcp(80);
        Ports portBindings = new Ports();
        portBindings.bind(tcp80,Ports.Binding.bindPort(30010));

        CreateContainerResponse response = dockerClient.
                createContainerCmd("nginx")
                .withImage("nginx")
                .withExposedPorts(tcp80)
                .withPortBindings(portBindings)
                .withAttachStderr(false)
                .withAttachStdin(false)
                .withAttachStdout(false)
                .exec();

        System.out.println(gson.toJson(response));
    }

    private static void startContainer(DockerClient dockerClient) {
        StartContainerCmd startContainerCmd = dockerClient.startContainerCmd("185117dff6b9dd66ef004050d7e15af47ff9c3264a58ef625c93194e78247064");
        startContainerCmd.exec();
    }

    private static void createContainer(Gson gson, DockerClient dockerClient) {
        CreateContainerCmd nginx = dockerClient.createContainerCmd("nginx");
        List<ExposedPort> exposedPorts = new ArrayList<>();
        ExposedPort p = new ExposedPort(30010);
        CreateContainerResponse exec = nginx.withExposedPorts(p).exec();
        System.out.println(gson.toJson(exec));
    }

    private static void listContainers(Gson gson, DockerClient dockerClient) {
        ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
        List<Container> exec = listContainersCmd.withShowAll(true).exec();
        System.out.println(gson.toJson(exec));
    }
}
