package container.service;

import com.github.dockerjava.api.model.Container;

import java.util.List;
import java.util.Map;

public interface ContainerService {
    /**
     * 列出所有容器，包含已经停止的容器
     * @return
     */
    List<Container> listAllContainers();

    /**
     * 创建容器但不运行
     * 返回创建的容器id
     * @param name
     * @return
     */
    String createContainer(String name, String imageId) ;
}
