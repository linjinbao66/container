package container.controller;

import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.model.Image;
import container.service.ImageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

@RestController
public class ImageController {
    private static final Logger LOG = LoggerFactory.getLogger(ImageService.class);
    @Autowired
    private ImageService imageService;

    @GetMapping("/image")
    @ResponseStatus(HttpStatus.OK)
    public String image(){
        return imageService.getAllImages();
    }


    @GetMapping("/image/{imageId}")
    @ResponseStatus(HttpStatus.OK)
    public String image(@PathVariable("imageId")String imageId){
        LOG.info("imageId = "+ imageId);
        return imageService.getByImageId(imageId);
    }

    @PostMapping("/image")
    @ResponseStatus(HttpStatus.CREATED)
    public String pullImage(@RequestParam(value = "imgName", required = true)String imgName,
                            @RequestParam(value = "tag", defaultValue = "latest") String tag) throws InterruptedException {
        LOG.info("imgName = " + imgName + " \t tag = " + tag);
        return imageService.pullImage(imgName,tag);
    }

    @GetMapping("/image/{imageName}/tag")
    @ResponseStatus(HttpStatus.CREATED)
    public String saveImage(@PathVariable(value = "imageName")String imageName,
                            @RequestParam(value = "tag", defaultValue = "latest")String tag,
                            @RequestParam(value = "outFile", required = true)String outFile,
                            HttpServletResponse response) throws UnsupportedEncodingException {
        LOG.info("imageName = " + imageName + "\t tag = " + tag + "\t outFile" + outFile);
        InputStream inputStream = imageService.saveImage(imageName, tag);
        BufferedInputStream bis = new BufferedInputStream(inputStream);;
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(outFile, "UTF-8"));
        byte[] buffer = new byte[1024];
        int length = 0;
        try {
            OutputStream os = response.getOutputStream();
            length = bis.read(buffer);
            while (length != -1) {
                os.write(buffer, 0, length);
                length = bis.read(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return "下载成功！";
    }

}
