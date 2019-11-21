package com.chen.java8;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 启动类
 * @author ChenTian
 * @date 2019/10/14
 */
@ServletComponentScan
@EnableScheduling
@EnableTransactionManagement
@SpringBootApplication
public class Java8ServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(Java8ServiceApplication.class, args);
    }
}
