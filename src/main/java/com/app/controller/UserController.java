package com.app.controller;

import com.app.config.LoggerService;
import com.app.dto.RegisterUserDto;
import com.app.facade.UserFacade;
import com.app.model.common.ResponseHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.app.util.MessageConstants.*;
import static com.app.util.Utils.tagMethodName;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "/api/users", description = "User APIs")
@Validated
public class UserController {

    @Autowired
    private UserFacade facade;
    @Autowired
    private LoggerService logger;

    private final String TAG = "UserController";

    /**
     * Get user data
     *
     * @return RegisterUserDto
     */
    @GetMapping("/me")
    public ResponseEntity<Object> authenticatedUser() {
        String methodName = "authenticatedUser";
        logger.request(tagMethodName(TAG, methodName), null);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        RegisterUserDto currentUser = (RegisterUserDto) authentication.getPrincipal();
        if (currentUser == null) {
            logger.response(tagMethodName(TAG, methodName), null);
            return ResponseHandler.failure(HttpStatus.NOT_FOUND, USER_NOT_FOUND);
        }
        logger.response(tagMethodName(TAG, methodName), currentUser);
        return ResponseHandler.success(HttpStatus.OK, currentUser, USER_FETCH_SUCCESS);
    }


    /**
     * Save user
     *
     * @param registerUserDto as RegisterUserDto
     * @return RegisterUserDto
     */
    @PostMapping("/register")
    @Operation(
            summary = "Save User",
            description = "Save user to the database",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User data to save",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RegisterUserDto.class)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User saved successfully",
                    content = @Content(
                            schema = @Schema(implementation = RegisterUserDto.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<?> saveUser(@Valid @RequestBody RegisterUserDto registerUserDto, BindingResult result) {
        String methodName = "saveUser";
        logger.request(tagMethodName(TAG, methodName), registerUserDto);
        if (result.hasErrors()) {
            logger.response(tagMethodName(TAG, methodName), result.getAllErrors());
            return ResponseHandler.failure(HttpStatus.BAD_REQUEST, result.getAllErrors().toString());
        }
        return ResponseEntity.ok(facade.saveUser(registerUserDto));
    }


    /**
     * Get user by id
     *
     * @param id as String
     * @return UserModel
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Get User by ID",
            description = "Fetch a user by their unique ID",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Unique User ID",
                            required = true,
                            in = ParameterIn.HEADER
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User data fetched successfully",
                    content = @Content(schema = @Schema(implementation = RegisterUserDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid User ID"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })

    public ResponseEntity<Object> getUserById(@PathVariable String id) {
        String methodName = "getUserById";
        logger.request(tagMethodName(TAG, methodName), id);

        if (id == null || id.trim().isEmpty()) {
            logger.warn(tagMethodName(TAG, methodName), "Invalid user ID received: {}");
            return ResponseHandler.failure(HttpStatus.BAD_REQUEST, INVALID_USER_ID);
        }

        Object result = facade.getUser(id);
        if (result == null) {
            logger.warn(tagMethodName(TAG, methodName), "User not found for ID: {}");
            return ResponseHandler.failure(HttpStatus.NOT_FOUND, USER_NOT_FOUND);
        }
        logger.response(tagMethodName(TAG, methodName), result);
        return ResponseHandler.success(HttpStatus.OK, result, USER_FETCH_SUCCESS);

    }


    /**
     * Get all users
     *
     * @return List of UserModel
     */
    @GetMapping("/all")
    @Operation(
            summary = "Get Users",
            description = "Retrieve a list of all users"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = RegisterUserDto.class))
                    )
            ),
            @ApiResponse(responseCode = "204", description = "No users found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Object> getUsers() {
        String methodName = "getUsers";
        logger.request(tagMethodName(TAG, methodName), null);
        try {
            List<RegisterUserDto> result = facade.getUsers();
            if (result == null || result.isEmpty()) {
                logger.warn(tagMethodName(TAG, methodName), "No users found in the database");
                return ResponseHandler.failure(HttpStatus.NO_CONTENT, NO_USERS_FOUND);
            }
            logger.response(tagMethodName(TAG, methodName), result.size());
            return ResponseHandler.success(HttpStatus.OK, result, USERS_FETCH_SUCCESS);
        } catch (Exception e) {
            logger.error(tagMethodName(TAG, methodName), "Unexpected error retrieving users", e);
            return ResponseHandler.failure(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_ERROR);
        }
    }
}