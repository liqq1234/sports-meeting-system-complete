package com.sports.medical;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.sports.medical.mapper")
public class MedicalApplication {
    public static void main(String[] args) {
        SpringApplication.run(MedicalApplication.class, args);
    }
}
