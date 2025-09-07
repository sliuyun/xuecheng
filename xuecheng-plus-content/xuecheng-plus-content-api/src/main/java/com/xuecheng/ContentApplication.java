package com.xuecheng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 课程内容模块启动类
 * 注意这个启动类必须在 com.xuecheng 包下，因为这个模块的groupId就是com.xuecheng
 */
@EnableFeignClients(basePackages={"com.xuecheng.content.feignclient"})

@SpringBootApplication
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}
