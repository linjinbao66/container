package container.service;

import java.io.InputStream;
import java.util.List;

public interface ImageService {
    List getAllImages();

    String getByImageId(String imageId);

    String pullImage(String imgName, String tag) throws InterruptedException;

    InputStream saveImage(String imgName, String tag);

    String loadImage(InputStream imageStream);

    String buildImage();
}
