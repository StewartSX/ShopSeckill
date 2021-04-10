package com.oracle.miaosha.miaosha_goods;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableEurekaClient
public class MiaoshaGoodsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiaoshaGoodsApplication.class, args);
    }

}
