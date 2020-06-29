package container.controller;

import container.service.ImageService;
import container.util.MesssageWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ImageController {
    private static final Logger LOG = LoggerFactory.getLogger(ImageService.class);
    @Autowired
    private ImageService imageService;

    /**
     * 查询镜像列表
     * @return
     */
    @GetMapping("/image")
    @ResponseStatus(HttpStatus.OK)
    public MesssageWrapper image(){
        List list = imageService.getAllImages();
        return new MesssageWrapper(0, null, list.size(), list);
    }

    /**
     * 根据镜像id查询镜像详情
     * @param imageId
     * @return
     */
    @GetMapping("/image/{imageId}")
    @ResponseStatus(HttpStatus.OK)
    public String image(@PathVariable("imageId")String imageId){
        LOG.info("imageId = "+ imageId);
        return imageService.getByImageId(imageId);
    }

    /***
     * 服务器根据指令去仓库下载镜像
     * @param imgName
     * @param tag
     * @return
     * @throws InterruptedException
     */
    @PostMapping("/image")
    @ResponseStatus(HttpStatus.CREATED)
    public String pullImage(@RequestParam(value = "imgName", required = true)String imgName,
                            @RequestParam(value = "tag", defaultValue = "latest") String tag) throws InterruptedException {
        LOG.info("imgName = " + imgName + " \t tag = " + tag);
        return imageService.pullImage(imgName,tag);
    }

    /**
     * 上传导出的镜像tar包，服务器 docker load
     * 单文件上传
     * @return
     */
    @PostMapping("/image/upload")
    @ResponseStatus(HttpStatus.OK)
    public String loadImage(@RequestParam(value = "file", required = true) MultipartFile file) throws IOException {
        if(file==null) return "上传失败，请选择文件";
        String fileName = file.getOriginalFilename();
        LOG.info("fileName = " + fileName);
        InputStream inputStream = file.getInputStream();
        return imageService.loadImage(inputStream);
    }

    /**
     * 镜像导出并下载
     * @param imageName
     * @param tag
     * @param outFile
     * @param response
     * @return
     * @throws UnsupportedEncodingException
     */
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
