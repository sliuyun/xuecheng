package com.xuecheng.media.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@FeignClient(value = "content-api")
public interface TeachplanServiceClient {

    @GetMapping("/content/r/teachplan/association/media")
    public int getAssociationMedia(@RequestParam("filemd5") String filemd5);
}
