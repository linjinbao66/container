package container.service.impl;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Ports;
import container.config.Client;
import container.service.ContainerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ContainerServiceImpl implements ContainerService {
    private static final Logger LOG = LoggerFactory.getLogger(ContainerServiceImpl.class);

    @Autowired
    private Client client;

    @Override
    public List<Container> listAllContainers() {
        LOG.info("listAllContainers");
        ListContainersCmd listContainersCmd = client.getDockerClient().listContainersCmd();
        return listContainersCmd.exec();
    }

    @Override
    public String createContainer(String name, String imageId) {

        return null;
    }
}
