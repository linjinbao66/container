package conainer;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.dockerfile.Dockerfile;
import com.github.dockerjava.core.dockerfile.DockerfileStatement;
import com.github.dockerjava.jaxrs.JerseyDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.google.gson.Gson;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestConnect {

    public static void main(String[] args) throws URISyntaxException, InterruptedException, IOException {
        Gson gson = new Gson();
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://192.168.43.128:2375")
                .build();

        DockerClient dockerClient = DockerClientBuilder
                .getInstance(config)
                .build();
        saveImage(dockerClient);
        return;
    }

    private static void saveImage(DockerClient dockerClient) throws IOException {
        SaveImageCmd busybox = dockerClient.saveImageCmd("tomcat:8.5");
        InputStream is = busybox.exec();
        File file = new File("D:\\aaa.tar");
        FileOutputStream fou = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int length = 0;
        while((length= is.read(buffer))>0){
            fou.write(buffer, 0, length);
        }
        is.close();
        fou.close();
    }

    /**
     * 搜索镜像
     * @param dockerClient
     */
    private static void searchImage(DockerClient dockerClient) {
        SearchImagesCmd busybox = dockerClient.searchImagesCmd("busybox");
        List<SearchItem> term = busybox.exec();
        System.out.println(term);
    }

    /**
     * 拉取镜像
     * @param dockerClient
     */
    private static void pullImage(DockerClient dockerClient) {
        PullImageCmd tomcat = dockerClient.pullImageCmd("tomcat").withTag("latest");
        tomcat.exec(new ResultCallback<PullResponseItem>(){
            @Override
            public void close() throws IOException {
                System.out.println("结束下载");
            }

            @Override
            public void onStart(Closeable closeable) {
                System.out.println("开始下载");
            }

            @Override
            public void onNext(PullResponseItem object) {
                System.out.println(object.getStatus());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {
                System.out.println("下载完毕!");
            }
        });
    }

    /**
     * 获取容器
     * @param gson
     * @param dockerClient
     */
    private static void listContainers(Gson gson, DockerClient dockerClient) {
        ListContainersCmd listContainersCmd = dockerClient.listContainersCmd();
        List<Container> containers = listContainersCmd.exec();
        containers.forEach(container ->{
            System.out.println(gson.toJson(container));
        });
    }

    /**
     * 列出镜像
     * @param gson
     * @param dockerClient
     */
    private static void listImages(Gson gson, DockerClient dockerClient) {
        ListImagesCmd listImagesCmd = dockerClient.listImagesCmd();
        List<Image> exec = listImagesCmd.exec();
        exec.forEach(img ->{
            gson.toJson(img);
            System.out.println(img);
        });
    }
}
