package com.secondhand;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 区块链二手买卖平台启动类
 */
@SpringBootApplication(excludeName = {
    "org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration",
    "org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration"
})
@MapperScan("com.secondhand.mapper")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
