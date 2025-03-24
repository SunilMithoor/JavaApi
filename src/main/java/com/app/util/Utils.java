package com.app.util;

import org.springframework.stereotype.Component;

@Component
public class Utils {

    public static String tagMethodName(String tag, String methodName) {
        try {
            return tag + ", " + methodName;
        } catch (Exception e) {
            return tag;
        }
    }
}
