package com.app;

import com.app.config.LoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Date;

import static com.app.util.Utils.tagMethodName;


@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = "com.app")
public class Application implements CommandLineRunner {

    private final LoggerService loggerService;
    private static final String TAG = "Application";

    @Autowired
    public Application(LoggerService loggerService) {
        this.loggerService = loggerService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String methodName = "application main";
        loggerService.info(tagMethodName(TAG, methodName),
                "Application Started: " + new Date(System.currentTimeMillis()));
    }
}
