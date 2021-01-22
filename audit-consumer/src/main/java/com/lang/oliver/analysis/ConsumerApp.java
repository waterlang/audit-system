package com.lang.oliver.analysis;


import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
@MapperScan("com.lang.oliver.analysis")
public class ConsumerApp {

    public static void main(String[] args) {
        try {
            SpringApplication.run(ConsumerApp.class, args);
        } catch (Exception e) {
            log.error("main start error", e);
        }

        log.info("app started");
    }

}
