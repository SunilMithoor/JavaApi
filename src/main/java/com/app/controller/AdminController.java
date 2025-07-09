package com.app.controller;

import com.app.config.LoggerService;
import com.app.facade.UserFacade;
import com.app.security.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@Tag(name = "/api/v1/admin", description = "Admin APIs")
@Validated
@SecurityRequirement(name = "Authorization")
public class AdminController {

    @Autowired
    private UserFacade facade;
    @Autowired
    private LoggerService logger;
    @Autowired
    private JwtUtil jwtUtil;
    private final String TAG = "AdminController";


}
