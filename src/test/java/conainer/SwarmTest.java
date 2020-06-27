package conainer;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectSwarmCmd;
import com.github.dockerjava.api.command.ListSwarmNodesCmd;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.google.gson.Gson;

import java.util.List;

public class SwarmTest {

    public static void main(String[] args) {
        Gson gson = new Gson();
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://192.168.43.128:2375")
                .build();

        DockerClient dockerClient = DockerClientBuilder
                .getInstance(config)
                .build();

        InspectSwarmCmd inspectSwarmCmd = dockerClient.inspectSwarmCmd();
        Swarm exec = inspectSwarmCmd.exec();

    }

    private static void getNodes(Gson gson, DockerClient dockerClient) {
        ListSwarmNodesCmd listSwarmNodesCmd = dockerClient.listSwarmNodesCmd();
        List<SwarmNode> exec = listSwarmNodesCmd.exec();

        System.out.println(gson.toJson(exec));
    }
}
