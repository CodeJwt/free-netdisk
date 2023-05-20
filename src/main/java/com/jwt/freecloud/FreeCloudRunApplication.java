package com.jwt.freecloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author：揭文滔
 * @since：2023/2/9
 */
@MapperScan("com.jwt.freecloud.dao")
@SpringBootApplication
public class FreeCloudRunApplication {
    public static void main(String[] args) {
        SpringApplication.run(FreeCloudRunApplication.class, args);
    }
}
