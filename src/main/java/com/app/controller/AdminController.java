package com.app.controller;

import com.app.config.LoggerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "/api/v1/admin", description = "Admin APIs")
@Validated
@SecurityRequirement(name = "Authorization")
public class AdminController {

    private final LoggerService loggerService;
    private static final String TAG = "AdminController";

    @Autowired
    public AdminController(LoggerService loggerService) {
        this.loggerService = loggerService;
    }


}
