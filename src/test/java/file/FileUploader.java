package file;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.util.List;


import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import io.minio.messages.Bucket;

public class FileUploader {
    public static void main(String[] args) throws IOException, InvalidKeyException, InvalidResponseException, InsufficientDataException, NoSuchAlgorithmException, ServerException, InternalException, XmlParserException, InvalidBucketNameException, ErrorResponseException {

        MinioClient client = MinioClient.builder()
                .endpoint("http://192.168.43.128:30022")
                .credentials("minioadmin","minioadmin")
                .build();

        String fileName = "D:\\文件备份\\数据治理--库表接口文档2.txt";

        ObjectWriteResponse demo1 = client.uploadObject(UploadObjectArgs.builder()
                .bucket("demo1")
                .object("数据治理.txt")
                .filename(fileName)
                .build()
        );
        String s = demo1.versionId();
        System.out.println(s);
    }
}
