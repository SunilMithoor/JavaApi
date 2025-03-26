package com.app.controller;


import com.app.config.LoggerService;
import com.app.dto.LoginUserDto;
import com.app.entity.User;
import com.app.facade.UserFacade;
import com.app.model.common.ResponseHandler;
import com.app.response.LoginUserResponseData;
import com.app.security.jwt.JwtUtil;
import com.app.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.app.util.Utils.tagMethodName;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "/api/auth", description = "Auth APIs")
@Validated
@SecurityRequirement(name = "Authorization")
public class AuthenticationController {

    @Autowired
    private UserFacade facade;
    @Autowired
    private LoggerService logger;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationService authenticationService;

    private final String TAG = "AuthenticationController";


    @PostMapping(name = "Login Api", value = "/login", consumes = {"application/json"})
    @Operation(
            summary = "Login User",
            description = "Login user",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login for the User",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoginUserDto.class)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User logged in successfully",
                    content = @Content(
                            schema = @Schema(implementation = LoginUserDto.class)
                    )
            ),
            @ApiResponse(responseCode = "401", description = "Request denied"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginUserDto loginUserDto, BindingResult result) {
        String methodName = "authenticateUser";
        logger.request(tagMethodName(TAG, methodName), loginUserDto);
        if (loginUserDto.getLoginId() == null || loginUserDto.getLoginId().isEmpty()) {
            logger.warn(tagMethodName(TAG, methodName), "Login ID is missing in request.");
            return ResponseHandler.failure(HttpStatus.BAD_REQUEST, "Login ID is required.");
        }
        logger.request(tagMethodName(TAG, methodName), result);
        if (result.hasErrors()) {
            logger.response(tagMethodName(TAG, methodName), result.getAllErrors());
            return ResponseHandler.failure(HttpStatus.BAD_REQUEST, result.getAllErrors().toString());
        }
        User authenticatedUser = facade.authenticate(loginUserDto);
        logger.response(tagMethodName(TAG, methodName), "AuthenticatedUser: " + authenticatedUser);
        if (authenticatedUser == null) {
            return ResponseHandler.failure(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        // Retrieve the old token from Redis
        String oldToken = jwtUtil.getUserToken(authenticatedUser.getUsername());
        if (oldToken != null) {
            jwtUtil.blacklistToken(oldToken, jwtUtil.getExpirationTime());
        }

        // Generate JWT Token
        String jwtToken = jwtUtil.generateToken(authenticatedUser);

        // Store the latest token in Redis for this user
        jwtUtil.storeUserToken(authenticatedUser.getUsername(), jwtToken, jwtUtil.getExpirationTime());

        logger.response(tagMethodName(TAG, methodName), "Jwt Token: " + jwtToken);

        LoginUserResponseData loginResponse = new LoginUserResponseData();
        loginResponse.setJwtToken(jwtToken);
        loginResponse.setExpiresIn(jwtUtil.getExpirationTime());
        logger.response(tagMethodName(TAG, methodName) , " LoginResponse: "+ loginResponse);
        return ResponseHandler.success(HttpStatus.OK, loginResponse, "Success");
    }

}