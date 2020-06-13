package container.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.PullResponseItem;
import com.google.gson.Gson;
import container.config.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ImageService {
    private static final Logger LOG = LoggerFactory.getLogger(ImageService.class);
    @Autowired
    private Client client;
    @Autowired
    private Gson gson;
    public String getAllImages(){
        DockerClient dockerClient = client.getDockerClient();
        ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();
        List<Image> exec = listImagesCmd.exec();
        return gson.toJson(exec);
    }

    public String getByImageId(String imageId) {
        DockerClient dockerClient = client.getDockerClient();
        InspectImageCmd inspectImageCmd = dockerClient.inspectImageCmd(imageId);
        InspectImageResponse exec = inspectImageCmd.exec();
        return gson.toJson(exec);
    }

    public String pullImage(String imgName, String tag) throws InterruptedException {
        DockerClient dockerClient = client.getDockerClient();
        List<String> stepsLog = new ArrayList<>();
        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(imgName).withTag(tag);
        pullImageCmd.exec(new PullImageResultCallback() {
            @Override
            public void onNext(PullResponseItem item) {
                LOG.info(item.toString());
                stepsLog.add(item.toString());
            }
        }).awaitCompletion(200, TimeUnit.SECONDS);

        return gson.toJson(stepsLog);
    }

    /**
     *
     * @param imgName
     * @param tag
     * @return
     */
    public InputStream saveImage(String imgName, String tag) {
        String result = null;
        DockerClient dockerClient = client.getDockerClient();
        LOG.info("保存镜像：" + imgName+":"+tag);
        SaveImageCmd saveImageCmd = dockerClient.saveImageCmd(imgName + ":" + tag);
        InputStream is = saveImageCmd.exec();
        return is;
    }

    public String loadImage(File imageFile) throws FileNotFoundException {
        DockerClient dockerClient = client.getDockerClient();
        InputStream imageStream = new FileInputStream(imageFile);
        LoadImageCmd loadImageCmd = dockerClient.loadImageCmd(imageStream);
        loadImageCmd.exec();
        return null;
    }
}
