package com.app.facade;


import com.app.config.LoggerService;
import com.app.dto.LoginUserDto;
import com.app.dto.RegisterUserDto;
import com.app.dto.UpdateUserDto;
import com.app.entity.User;
import com.app.exception.custom.InvalidParamException;
import com.app.exception.custom.UserAlreadyExistException;
import com.app.exception.custom.UserNotFoundException;
import com.app.service.AuthenticationService;
import com.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.app.util.MessageConstants.*;
import static com.app.util.Utils.tagMethodName;

@Service
public class UserFacade {


    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final LoggerService logger;

    private static final String TAG = "UserFacade";

    @Autowired
    public UserFacade(UserService userService,AuthenticationService authenticationService, ModelMapper modelMapper,
                      PasswordEncoder passwordEncoder,AuthenticationManager authenticationManager,LoggerService logger)
    {
        this.userService=userService;
        this.authenticationService=authenticationService;
        this.modelMapper=modelMapper;
        this.passwordEncoder=passwordEncoder;
        this.authenticationManager=authenticationManager;
        this.logger=logger;

    }

    /**
     * Get user by id
     */
    public User getUser(Long userId) {
        String methodName = "getUser";
        logger.request(tagMethodName(TAG, methodName), "User id: " + userId);
        if (userId == null || userId <= 0) {
            logger.error(tagMethodName(TAG, methodName), "User Id is required", null);
            throw new InvalidParamException(USER_ID_REQUIRED);
        }
        try {
            User user = userService.getUserById(userId);
            logger.info(tagMethodName(TAG, methodName), "User: " + user);
            if (user == null) {
                logger.error(tagMethodName(TAG, methodName), "User: " + null, null);
                throw new UserNotFoundException();
            }
            logger.response(tagMethodName(TAG, methodName), "User data: " + user);
            return modelMapper.map(user, User.class);
        } catch (NumberFormatException e) {
            logger.error(tagMethodName(TAG, methodName), "User Id is required", null);
            throw new InvalidParamException(USER_ID_VALID_NUMBER);
        }
    }


    /**
     * Get all users
     *
     * @return List of UserModel (DTO)
     */
    public List<RegisterUserDto> getUsers() {
        String methodName = "getUsers";
        logger.request(tagMethodName(TAG, methodName), "Get users ");
        List<User> users = userService.getAllUsers();
        logger.response(tagMethodName(TAG, methodName), users);
        if (users == null || users.isEmpty()) {
            logger.error(tagMethodName(TAG, methodName), "User not found", null);
            throw new UserNotFoundException();
        }
        return users.stream()
                .map(user -> modelMapper.map(user, RegisterUserDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Save user
     *
     * @param registerUserDto as RegisterUserDto
     * @return RegisterUserDto (DTO)
     */

    public RegisterUserDto saveUser(RegisterUserDto registerUserDto) {
        String methodName = "saveUser";
        logger.request(tagMethodName(TAG, methodName), registerUserDto);
        if (registerUserDto == null) {
            logger.error(tagMethodName(TAG, methodName), "User data cannot be null",null);
            throw new InvalidParamException(USER_DATA_NULL);
        }

        // Check if user already exists
        if (registerUserDto.getId() != null) {
            User existingUser = userService.getUserById(registerUserDto.getId());
            logger.response(tagMethodName(TAG, methodName), existingUser);
            if (existingUser != null) {
                logger.error(tagMethodName(TAG, methodName), "User already exist",null);
                throw new UserAlreadyExistException();
            }
        }

        if (registerUserDto.getEmailId() == null || registerUserDto.getEmailId().trim().isEmpty()) {
            logger.error(tagMethodName(TAG, methodName), "User Email Id is null",null);
            throw new InvalidParamException(EMAIL_ID_VALID);
        }

        // Check if email already exists
        if (userService.isEmailExists(registerUserDto.getEmailId())) {
            logger.error(tagMethodName(TAG, methodName), "User Email already exists",null);
            throw new InvalidParamException(EMAIL_ID_ALREADY_REGISTERED);
        }

        if (registerUserDto.getMobileNo() == null || registerUserDto.getMobileNo().trim().isEmpty()) {
            logger.error(tagMethodName(TAG, methodName), "Mobile no required",null);
            throw new InvalidParamException(MOBILE_NUMBER_REQUIRED);
        }

        // Check if mobile number already exists
        if (userService.isMobileExists(registerUserDto.getMobileNo())) {
            logger.error(tagMethodName(TAG, methodName), "User mobile no already exist",null);
            throw new InvalidParamException(MOBILE_NUMBER_ALREADY_REGISTERED);
        }

        // Map DTO to Entity
        User user = modelMapper.map(registerUserDto, User.class);
        logger.response(tagMethodName(TAG, methodName), user);
        if (registerUserDto.getPassword() == null || registerUserDto.getPassword().trim().isEmpty()) {
            logger.error(tagMethodName(TAG, methodName), "User password cannot be null",null);
            throw new InvalidParamException(PASSWORD_NULL);
        }

        user.setUserName(registerUserDto.getFirstName() + "@" + registerUserDto.getMobileNo());
        user.setPasswordHash(passwordEncoder.encode(registerUserDto.getPassword()));

        // Save User
        user = userService.saveUser(user);
        logger.response(tagMethodName(TAG, methodName), user);
        // Return saved user as DTO
        return modelMapper.map(user, RegisterUserDto.class);
    }

    /**
     * Update user
     *
     * @param updateUserDto as UpdateUserDto
     * @return UpdateUserDto (DTO)
     */

    public UpdateUserDto updateUser(UpdateUserDto updateUserDto) {
        String methodName = "updateUser";
        logger.request(tagMethodName(TAG, methodName), updateUserDto);
        if (updateUserDto == null) {
            logger.error(tagMethodName(TAG, methodName), "User data cannot be null",null);
            throw new InvalidParamException(USER_DATA_NULL);
        }

        // Check if user ID is provided
        if (updateUserDto.getId() == null) {
            logger.error(tagMethodName(TAG, methodName), "User id is required",null);
            throw new InvalidParamException(USER_ID_REQUIRED);
        }

        // Fetch existing user
        User existingUser = userService.getUserById(updateUserDto.getId());
        if (existingUser == null) {
            throw new InvalidParamException("User not found");
        }

        // Validate email if provided
        if (updateUserDto.getEmailId() != null && !updateUserDto.getEmailId().trim().isEmpty()) {
            if (!updateUserDto.getEmailId().equals(existingUser.getEmailId()) && userService.isEmailExists(updateUserDto.getEmailId())) {
                throw new InvalidParamException(EMAIL_ID_ALREADY_REGISTERED);
            }
        }

        // Validate mobile number if provided
        if (updateUserDto.getMobileNo() != null && !updateUserDto.getMobileNo().trim().isEmpty()) {
            if (!updateUserDto.getMobileNo().equals(existingUser.getMobileNo()) && userService.isMobileExists(updateUserDto.getMobileNo())) {
                throw new InvalidParamException(MOBILE_NUMBER_ALREADY_REGISTERED);
            }
        }

        // Update existing user's details
        modelMapper.map(updateUserDto, existingUser);

        // Ensure the username follows the same pattern if mobile number is updated
        if (updateUserDto.getMobileNo() != null && !updateUserDto.getMobileNo().trim().isEmpty()) {
            existingUser.setUserName(existingUser.getFirstName() + "@" + updateUserDto.getMobileNo());
        }

        // Update password if provided
        if (updateUserDto.getPassword() != null && !updateUserDto.getPassword().trim().isEmpty()) {
            existingUser.setPasswordHash(passwordEncoder.encode(updateUserDto.getPassword()));
        }

        // Save the updated user
        User updatedUser = userService.updateUser(existingUser);
        logger.response(tagMethodName(TAG, methodName), updatedUser);
        // Convert back to DTO and return
        return modelMapper.map(updatedUser, UpdateUserDto.class);
    }


    /**
     * User authenticate
     */
    public User authenticate(LoginUserDto input) {
        String methodName = "authenticate";
        if (input == null) {
            throw new InvalidParamException("User data cannot be null");
        }

        logger.info(tagMethodName(TAG, methodName), "Authenticating user with loginId: " + input.getLoginId());
        logger.info(tagMethodName(TAG, methodName), "Received password: " + (input.getPassword() != null ? "******" : "NULL"));

        if (input.getLoginId() == null || input.getLoginId().trim().isEmpty()) {
            logger.info(tagMethodName(TAG, methodName), "Login ID is missing or empty");
            throw new InvalidParamException("Login ID cannot be empty");
        }

        // Fetch user using email, mobile number, or username
        User user = authenticationService.findByEmailIdOrMobileNoOrUserName(input.getLoginId())
                .orElseThrow(() -> {
                    logger.warn(tagMethodName(TAG, methodName), "User not found for loginId: " + input.getLoginId());
                    return new UserNotFoundException();
                });

        // Authenticate user
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(input.getLoginId(), input.getPassword())
            );
            logger.info(tagMethodName(TAG, methodName), "User authentication successful for: " + input.getLoginId());
        } catch (Exception e) {
            logger.error(tagMethodName(TAG, methodName), "Authentication failed for user: " + input.getLoginId(), e);
            throw new BadCredentialsException("Invalid credentials");
        }

        User response = new User();
        response.setUserName(user.getUsername());
        response.setPasswordHash(user.getPasswordHash());
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setMobileNo(user.getMobileNo());
        response.setEmailId(user.getEmailId());
        response.setRole(user.getRole());
        return response;
    }

}

