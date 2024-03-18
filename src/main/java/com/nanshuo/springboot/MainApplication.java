package com.nanshuo.springboot;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 主要应用程序（项目启动入口）
 *
 * @author 小鱼儿
 * @date 2023/12/23 00:00:00
 */
@Slf4j
@EnableAspectJAutoProxy
@MapperScan("com.nanshuo.springboot.mapper")
@SpringBootApplication
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
        log.info("Project started successfully！");
    }

}
