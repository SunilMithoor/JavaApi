package com.app.model.common;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {

    private ResponseHandler() {
    }

    public static ResponseEntity<Object> success(HttpStatus status, Object data, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", new Date());
        response.put("status_code", status.value());
        response.put("success", true);
        response.put("data", data);
        response.put("message", message);
        return new ResponseEntity<>(response, status);
    }


    public static ResponseEntity<Object> failure(HttpStatus status, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", new Date());
        response.put("status_code", status.value());
        response.put("success", false);
        response.put("data", null);
        response.put("message", message);
        return new ResponseEntity<>(response, status);
    }
}
