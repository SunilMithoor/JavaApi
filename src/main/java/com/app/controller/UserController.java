package com.app.controller;

import com.app.config.LoggerService;
import com.app.dto.request.LoginUser;
import com.app.dto.request.RegisterUser;
import com.app.dto.request.UpdateUser;
import com.app.entity.User;
import com.app.exception.custom.InvalidParamException;
import com.app.exception.custom.UserAlreadyExistException;
import com.app.facade.UserFacade;
import com.app.model.common.ResponseHandler;
import com.app.dto.response.RegisterUserData;
import com.app.security.jwt.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.app.util.MessageConstants.*;
import static com.app.util.Utils.tagMethodName;


@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "/api/v1/users", description = "User APIs")
@Validated
@SecurityRequirement(name = "Authorization")
public class UserController {


    private final UserFacade facade;
    private final LoggerService logger;
    private final JwtUtil jwtUtil;
    private static final String TAG = "UserController";

    @Autowired
    public UserController(UserFacade userFacade, LoggerService logger, JwtUtil jwtUtil) {
        this.facade = userFacade;
        this.logger = logger;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Save user
     *
     * @param registerUser as RegisterUser
     * @return RegisterUser
     */
    @PostMapping("/register")
    @Operation(
            summary = "Save User",
            description = "Save user to the database",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User data to save",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RegisterUser.class)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User saved successfully",
                    content = @Content(
                            schema = @Schema(implementation = RegisterUser.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<?> saveUser(@Valid @RequestBody RegisterUser registerUser, BindingResult result) {
        String methodName = "saveUser";
        logger.request(tagMethodName(TAG, methodName), registerUser);
        if (result.hasErrors()) {
            logger.response(tagMethodName(TAG, methodName), result.getAllErrors());
            return ResponseHandler.failure(HttpStatus.BAD_REQUEST, result.getAllErrors().toString());
        }
        try {
            RegisterUser savedUser = facade.saveUser(registerUser);

            LoginUser loginUser = new LoginUser();
            loginUser.setLoginId(registerUser.getEmailId());
            loginUser.setPassword(registerUser.getPassword());
            User authenticatedUser = facade.authenticate(loginUser);
            logger.response(tagMethodName(TAG, methodName) + "AuthenticatedUser: ", authenticatedUser);
            if (authenticatedUser == null) {
                return ResponseHandler.failure(HttpStatus.UNAUTHORIZED, "Invalid email or password");
            }
            // Generate JWT Token
            String jwtToken = jwtUtil.generateToken(authenticatedUser);

            // Store the latest token in Redis for this user
            jwtUtil.storeUserToken(authenticatedUser.getUsername(), jwtToken, jwtUtil.getExpirationTime());

            logger.response(tagMethodName(TAG, methodName) + "Jwt Token: ", jwtToken);

            // Prepare response
            RegisterUserData response = new RegisterUserData(
                    jwtToken,
                    savedUser.getId(),
                    savedUser.getFirstName(),
                    savedUser.getLastName(),
                    savedUser.getUsername(),
                    savedUser.getEmailId(),
                    savedUser.getCountryCode(),
                    savedUser.getMobileNo(),
                    savedUser.getRole()
            );
            return ResponseHandler.success(HttpStatus.OK, response, "User registered successfully");
        } catch (UserAlreadyExistException e) {
            return ResponseHandler.failure(HttpStatus.CONFLICT, "User already exists");
        } catch (InvalidParamException e) {
            return ResponseHandler.failure(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error(tagMethodName(TAG, methodName), "Unexpected error occurred while saving user", e);
            return ResponseHandler.failure(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }


    /**
     * Get user by id
     *
     * @param id as String
     * @return UserModel
     */
    @GetMapping()
    @Operation(
            summary = "Get User by ID",
            description = "Fetch a user by their unique ID",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Unique User ID",
                            required = true,
                            in = ParameterIn.QUERY // Change this to QUERY
                    )
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User data fetched successfully",
                    content = @Content(schema = @Schema(implementation = RegisterUser.class))),
            @ApiResponse(responseCode = "400", description = "Invalid User ID"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })

    public ResponseEntity<Object> getUserById(@RequestParam("id") Long id) {
        String methodName = "getUserById";
        logger.request(tagMethodName(TAG, methodName), id);

        if (id == null || id <= 0) {
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

    @GetMapping("/me")
    @Operation(summary = "Get User by Token", description = "Fetch the logged-in user using JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User data fetched successfully",
                    content = @Content(schema = @Schema(implementation = RegisterUser.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Object> getUserByToken(@RequestHeader("Authorization") String token) {
        String methodName = "getUserByToken";
        logger.request(tagMethodName(TAG, methodName), token);

        if (token == null || !token.startsWith("Bearer ")) {
            logger.response(tagMethodName(TAG, methodName), "Invalid or missing token");
            return ResponseHandler.failure(HttpStatus.UNAUTHORIZED, "Invalid or missing token");
        }

        token = token.substring(7); // Remove "Bearer " prefix
        logger.response(tagMethodName(TAG, methodName), "Substring token: " + token);
        Long userId = jwtUtil.extractUserId(token);

        if (userId == null) {
            return ResponseHandler.failure(HttpStatus.UNAUTHORIZED, "Invalid token");
        }

        Object user = facade.getUser(userId);
        if (user == null) {
            return ResponseHandler.failure(HttpStatus.NOT_FOUND, "User not found");
        }

        return ResponseHandler.success(HttpStatus.OK, user, "User fetched successfully");
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
                            array = @ArraySchema(schema = @Schema(implementation = RegisterUser.class))
                    )
            ),
            @ApiResponse(responseCode = "204", description = "No users found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Object> getUsers() {
        String methodName = "getUsers";
        logger.request(tagMethodName(TAG, methodName), null);
        try {
            List<RegisterUser> result = facade.getUsers();
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


    @PatchMapping("/update")
    @Operation(
            summary = "Update User",
            description = "Update user details in the database",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User data to update",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UpdateUser.class)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(
                            schema = @Schema(implementation = UpdateUser.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUser updateUser, BindingResult result) {
        String methodName = "updateUser";
        logger.request(tagMethodName(TAG, methodName), updateUser);

        if (result.hasErrors()) {
            logger.response(tagMethodName(TAG, methodName), result.getAllErrors());
            return ResponseHandler.failure(HttpStatus.BAD_REQUEST, result.getAllErrors().toString());
        }

        try {
            // Check if the user exists
            User existingUser = facade.getUser(updateUser.getId());
            if (existingUser == null) {
                return ResponseHandler.failure(HttpStatus.NOT_FOUND, "User not found");
            }

            // Update user details
            UpdateUser updatedUser = facade.updateUser(updateUser);
            logger.response(tagMethodName(TAG, methodName), updatedUser);

            return ResponseHandler.success(HttpStatus.OK, updatedUser, "User updated successfully");
        } catch (InvalidParamException e) {
            return ResponseHandler.failure(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error(tagMethodName(TAG, methodName), "Unexpected error occurred while updating user", e);
            return ResponseHandler.failure(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }


}