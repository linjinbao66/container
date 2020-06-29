package container.controller;

import container.service.ContainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContainerController {

    @Autowired
    ContainerService containerService;

}
