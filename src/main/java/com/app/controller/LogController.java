package com.app.controller;

import com.app.config.LoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/log")
public class LogController {

    private final LoggerService loggerService;

    @Autowired
    public LogController(LoggerService loggerService) {
        this.loggerService = loggerService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> testLogging() {
        loggerService.process();
        return ResponseEntity.ok("Logging executed successfully");
    }
}
