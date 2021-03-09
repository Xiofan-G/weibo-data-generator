package com.weibo.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class WeiboDataGeneratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeiboDataGeneratorApplication.class, args);
    }

}
