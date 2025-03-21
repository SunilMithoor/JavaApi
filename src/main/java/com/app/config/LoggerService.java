package com.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;


@Service
public class LoggerService {
    private static final Logger logger = LoggerFactory.getLogger(LoggerService.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public void request(String tag, String methodName, Object request) {
        try {
            logger.info("[{}] Received request for {}: {}", tag, methodName, objectMapper.writeValueAsString(request));
        } catch (Exception e) {
            logger.error("[{}] Failed to log request for {}: {}", tag, methodName, e.getMessage());
        }
    }

    public void response(String tag, String methodName, Object response) {
        try {
            logger.info("[{}] Retrieved response for {}: {}", tag, methodName, objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            logger.error("[{}] Failed to log response for {}: {}", tag, methodName, e.getMessage());
        }
    }

    public void info(String tag, String message) {
        MDC.put("TAG", tag);
        logger.info(message);
        MDC.clear();
    }

    public void warn(String tag, String message) {
        MDC.put("TAG", tag);
        logger.warn(message);
        MDC.clear();
    }

    public void error(String tag, String message, Throwable throwable) {
        MDC.put("TAG", tag);
        logger.error(message, throwable);
        MDC.clear();
    }
}
