package file;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidKeyException;
import java.util.Iterator;
import java.util.List;


import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;

public class FileUploader {
    public static void main(String[] args) throws Exception {

        MinioClient client = MinioClient.builder()
                .endpoint("http://192.168.43.128:30022")
                .credentials("minioadmin","minioadmin")
                .build();

        Iterable<Result<Item>> demo1 = client.listObjects(ListObjectsArgs.builder()
                .bucket("demo1")
                .build()
        );

        for (Iterator iter = demo1.iterator(); iter.hasNext();){
            Result result = (Result) iter.next();
            System.out.println(result.get());
        }


    }
}

