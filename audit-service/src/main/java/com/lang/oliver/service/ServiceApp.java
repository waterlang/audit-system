package com.lang.oliver.service;


import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@Slf4j
@SpringBootApplication
@MapperScan("com.lang.oliver.service.repository")
public class ServiceApp {

    public static void main(String[] args) {
        try{
            SpringApplication.run(ServiceApp.class,args);
        }catch (Exception e){
            log.error("main start error",e);
        }

        log.info("app started");
    }

}
