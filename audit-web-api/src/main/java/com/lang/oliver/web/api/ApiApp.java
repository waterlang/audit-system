package com.lang.oliver.web.api;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class ApiApp {


    public static void main(String[] args) {
       try{
           SpringApplication.run(ApiApp.class,args);
       }catch (Exception e){
           log.error("main start error",e);
       }

       log.info("app started");

    }



}
