package org.stonex.common.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringCloudApplication
//@EnableHystrix
//@EnableFeignClients(basePackages = {"com.itheima.security.distributed.uaa"})
public class StoneXAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(StoneXAuthApplication.class,args);
    }
}
