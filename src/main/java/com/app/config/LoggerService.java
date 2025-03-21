package com.app.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggerService {
    private static final Logger logger = LoggerFactory.getLogger(LoggerService.class);

    public void process() {
        logger.info("INFO: Processing started");
        logger.warn("WARN: This is a warning message");
        logger.error("ERROR: An error occurred");
    }
}
