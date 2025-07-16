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


    public void request(String tagMethodName, Object request) {
        if (logger.isInfoEnabled()) {
            try {
                logger.info("[{}] Received request for : {}", tagMethodName, objectMapper.writeValueAsString(request));
            } catch (Exception e) {
                logger.error("[{}] Failed to log request for : {}", tagMethodName, e.getMessage());
            }
        }
    }


    public void response(String tagMethodName,  Object response) {
        if (logger.isInfoEnabled()) {
            try {
                logger.info("[{}] Retrieved response for : {}", tagMethodName, objectMapper.writeValueAsString(response));
            } catch (Exception e) {
                logger.error("[{}] Failed to log response for : {}", tagMethodName, e.getMessage());
            }
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
