package container.service.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.PullResponseItem;
import com.google.gson.Gson;
import container.config.Client;
import container.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ImageServiceImpl implements ImageService {
    private static final Logger LOG = LoggerFactory.getLogger(ImageServiceImpl.class);
    @Autowired
    private Client client;
    @Autowired
    private Gson gson;


    @Override
    public List<Image> getAllImages(){
        DockerClient dockerClient = client.getDockerClient();
        ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();
        List<Image> exec = listImagesCmd.exec();
        return exec;
    }

    @Override
    public String getByImageId(String imageId) {
        DockerClient dockerClient = client.getDockerClient();
        InspectImageCmd inspectImageCmd = dockerClient.inspectImageCmd(imageId);
        InspectImageResponse exec = inspectImageCmd.exec();
        return gson.toJson(exec);
    }

    @Override
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
    @Override
    public InputStream saveImage(String imgName, String tag) {
        String result = null;
        DockerClient dockerClient = client.getDockerClient();
        LOG.info("保存镜像：" + imgName+":"+tag);
        SaveImageCmd saveImageCmd = dockerClient.saveImageCmd(imgName + ":" + tag);
        InputStream is = saveImageCmd.exec();
        return is;
    }

    /**
     * 导入镜像的tar包
     * @param imageStream
     * @return
     */
    @Override
    public String loadImage(InputStream imageStream) {
        DockerClient dockerClient = client.getDockerClient();
        LoadImageCmd loadImageCmd = dockerClient.loadImageCmd(imageStream);
        try {
            loadImageCmd.exec();
            return "导入成功";
        }catch (Exception e){
            return "导入失败" + e.getMessage();
        }
    }

    /**
     *
     * @return
     */
    @Override
    public String buildImage(){
        DockerClient dockerClient = client.getDockerClient();
        dockerClient.buildImageCmd();
        return null;
    }
}
