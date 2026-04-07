package com.sports.logistics;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.sports.logistics.mapper")
public class LogisticsApplication {
    public static void main(String[] args) {
        SpringApplication.run(LogisticsApplication.class, args);
    }
}
