package com.app.controller;

import com.app.config.LoggerService;
import com.app.dto.UserRequestDTO;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.app.util.Constants.UserPaths.USER_PATH;
import static com.app.util.MessageConstants.*;


@RestController
@RequiredArgsConstructor
@RequestMapping(USER_PATH)
@Tag(name = USER_PATH, description = "User APIs")
@Validated
public class UserController {

    @Autowired
    private UserFacade facade;
    @Autowired
    private LoggerService logger;

    private final String TAG = "UserController";


    /**
     * Save user
     *
     * @param userDto as UserRequestDTO
     * @return UserRequestDTO
     */
    @PostMapping
    @Operation(
            summary = "Save User",
            description = "Save user to the database",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User data to save",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UserRequestDTO.class)
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User saved successfully",
                    content = @Content(
                            schema = @Schema(implementation = UserRequestDTO.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<?> saveUser(@Valid @RequestBody UserRequestDTO userDto, BindingResult result) {
        String methodName = "saveUser";
        logger.request(TAG, methodName, userDto);
        if (result.hasErrors()) {
            logger.response(TAG, methodName, result.getAllErrors());
            return ResponseHandler.failure(HttpStatus.BAD_REQUEST, result.getAllErrors().toString());
        }
        return ResponseEntity.ok(facade.saveUser(userDto));
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
                    content = @Content(schema = @Schema(implementation = UserRequestDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid User ID"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })

    public ResponseEntity<Object> getUserById(@PathVariable String id) {
        String methodName = "getUserById";
        logger.request(TAG,methodName, id);

        if (id == null || id.trim().isEmpty()) {
            logger.warn(TAG, "Invalid user ID received: {}");
            return ResponseHandler.failure(HttpStatus.BAD_REQUEST, INVALID_USER_ID);
        }

        Object result = facade.getUser(id);
        if (result == null) {
            logger.warn(TAG, "User not found for ID: {}");
            return ResponseHandler.failure(HttpStatus.NOT_FOUND, USER_NOT_FOUND);
        }
        logger.response(TAG, methodName,result);
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
                            array = @ArraySchema(schema = @Schema(implementation = UserRequestDTO.class))
                    )
            ),
            @ApiResponse(responseCode = "204", description = "No users found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<Object> getUsers() {
        String methodName = "getUsers";
        logger.request(TAG, methodName,null);
        try {
            List<UserRequestDTO> result = facade.getUsers();
            if (result == null || result.isEmpty()) {
                logger.warn(TAG, "No users found in the database");
                return ResponseHandler.failure(HttpStatus.NO_CONTENT, NO_USERS_FOUND);
            }
            logger.response(TAG, methodName,result.size());
            return ResponseHandler.success(HttpStatus.OK, result, USERS_FETCH_SUCCESS);
        } catch (Exception e) {
            logger.error(TAG, "Unexpected error retrieving users", e);
            return ResponseHandler.failure(HttpStatus.INTERNAL_SERVER_ERROR, INTERNAL_ERROR);
        }
    }
}