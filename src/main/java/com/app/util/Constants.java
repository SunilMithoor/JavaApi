package com.app.util;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constants {

    public static String BASE_URL = "/api";

    @Value("${app.base-url:/api}")  // Inject from properties with a default
    public void setBaseUrl(String baseUrl) {
        BASE_URL = baseUrl;
    }

    public static final String AUTH_PATH = BASE_URL + "/auth";
    public static final String USER_PATH = BASE_URL + "/users";

}
