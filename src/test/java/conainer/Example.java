package conainer;

import com.google.common.io.ByteStreams;
import com.google.gson.reflect.TypeToken;
import io.kubernetes.client.PodLogs;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.KubeConfig;
import io.kubernetes.client.util.Watch;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Example {
    public static void main(String[] args) throws IOException, ApiException {

        String kubeConfigPath = "D:\\code\\container\\src\\test\\resources\\config";

        ApiClient client =
                ClientBuilder
                        .kubeconfig(KubeConfig
                                .loadKubeConfig(new FileReader(kubeConfigPath))).build();

        Configuration.setDefaultApiClient(client);

        CoreV1Api coreApi = new CoreV1Api();

        PodLogs logs = new PodLogs();
        List<V1Pod> pods =
                coreApi
                        .listNamespacedPod("default", "false", null, null, null, null, null, null, null, null)
                        .getItems();

        pods.forEach(pod -> {
            pod.getStatus();
            System.out.println(pod.getMetadata().getName());


        });
//        InputStream is = logs.streamNamespacedPodLog(pod);
//        ByteStreams.copy(is, System.out);
    }

}
