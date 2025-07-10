package com.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Date;


@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = "com.app")
public class Application {


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("Application Started: " + new Date(System.currentTimeMillis()));
    }


}
