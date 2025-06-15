package com.app.exception_handler;

import com.app.config.LoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalControllerAdvice {
    @Autowired
    private LoggerService logger;
    private final String TAG = "GlobalControllerAdvice";

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnhandledExceptions(Exception ex) {
        logger.error(TAG+" Unhandled exception: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unhandled exception");
    }

    private ResponseEntity<Object> buildResponse(HttpStatus status, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", new Date());
        errorResponse.put("status_code", status.value());
        errorResponse.put("success", false);
        errorResponse.put("data", null);
        errorResponse.put("message", message);

        return new ResponseEntity<>(errorResponse, status);
    }
}
