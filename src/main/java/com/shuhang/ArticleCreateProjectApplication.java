package com.shuhang;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
@MapperScan("com.shuhang.mapper")
public class ArticleCreateProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(ArticleCreateProjectApplication.class, args);
    }

}
