package container.controller;


import container.pojo.WebSSHData;
import container.util.RedisUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RequestMapping("/redis")
@RestController
public class RedisController {

    private static int ExpireTime = 60;   // redis中存储的过期时间60s

    @Resource
    private RedisUtil redisUtil;

    @RequestMapping("/set")
    public boolean redisset(@RequestParam("key") String key){
        WebSSHData webSSHData =new WebSSHData();
        webSSHData.setCommand("ls");
        webSSHData.setHost("192.168.23132");
        webSSHData.setPassword("sada");
        webSSHData.setOperate("sdad");
        webSSHData.setUsername("linjb");

        return redisUtil.set(key,webSSHData);
    }

    @RequestMapping("/get")
    public Object redisget(@RequestParam("key") String key){
        return redisUtil.get(key);
    }

    @RequestMapping("/expire")
    public boolean expire(@RequestParam("key") String key){
        return redisUtil.expire(key,ExpireTime);
    }
}
