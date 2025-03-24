package com.app.facade;


import com.app.config.LoggerService;
import com.app.dto.LoginUserDto;
import com.app.dto.RegisterUserDto;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.app.util.MessageConstants.*;
import static com.app.util.Utils.tagMethodName;

@Service
@RequiredArgsConstructor
public class UserFacade {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private LoggerService logger;

    private final String TAG = "UserFacade";

    /**
     * Get user by id
     */
    public RegisterUserDto getUser(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidParamException(USER_ID_REQUIRED);
        }
        try {
            Long userId = Long.parseLong(id);
            User user = userService.getUserById(userId);

            if (user == null) {
                throw new UserNotFoundException();
            }
            return modelMapper.map(user, RegisterUserDto.class);
        } catch (NumberFormatException e) {
            throw new InvalidParamException(USER_ID_VALID_NUMBER);
        }
    }

    public RegisterUserDto getUserByEmailId(String emailId) {
        if (emailId == null || emailId.trim().isEmpty()) {
            throw new InvalidParamException(USER_EMAIL_ID_REQUIRED);
        }
        try {
            User user = userService.getUserByEmailId(emailId);

            if (user == null) {
                throw new UserNotFoundException();
            }
            return modelMapper.map(user, RegisterUserDto.class);
        } catch (NumberFormatException e) {
            throw new InvalidParamException(USER_ID_VALID_NUMBER);
        }
    }


    /**
     * Get all users
     *
     * @return List of UserModel (DTO)
     */
    public List<RegisterUserDto> getUsers() {
        List<User> users = userService.getAllUsers();
        if (users == null || users.isEmpty()) {
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
        if (registerUserDto == null) {
            throw new InvalidParamException("User data cannot be null");
        }

        if (registerUserDto.getId() != null) {
            User existingUser = userService.getUserById(registerUserDto.getId());
            if (existingUser != null) {
                throw new UserAlreadyExistException();
            }
        }

        if (registerUserDto.getEmailId() == null || registerUserDto.getEmailId().trim().isEmpty()) {
            throw new InvalidParamException(EMAIL_ID_VALID);
        }

        // Check if an email already exists
        if (registerUserDto.getEmailId() != null) {
            boolean exists = userService.isEmailExists(registerUserDto.getEmailId());
            if (exists) {
                throw new InvalidParamException(EMAIL_ID_ALREADY_REGISTERED);
            }
        }

        if (registerUserDto.getMobileNo() == null || registerUserDto.getMobileNo().trim().isEmpty()) {
            throw new InvalidParamException(MOBILE_NUMBER_REQUIRED);
        }

        // Check if a mobile no already exists
        if (registerUserDto.getMobileNo() != null) {
            boolean exists = userService.isMobileExists(registerUserDto.getMobileNo());
            if (exists) {
                throw new InvalidParamException(MOBILE_NUMBER_ALREADY_REGISTERED);
            }
        }

        User user = modelMapper.map(registerUserDto, User.class);
//        user.setUserName(registerUserDto.getUsername());
//        user.setFirstName(registerUserDto.getFirstName());
//        user.setLastName(registerUserDto.getLastName());

        if (registerUserDto.getPassword() == null || registerUserDto.getPassword().trim().isEmpty()) {
            throw new InvalidParamException("Password cannot be null or empty");
        }
        user.setUserName(registerUserDto.getFirstName()+"@"+registerUserDto.getMobileNo());
        user.setPasswordHash(passwordEncoder.encode(registerUserDto.getPassword()));

        user = userService.saveUser(user);

        return modelMapper.map(user, RegisterUserDto.class);
    }

    public RegisterUserDto updateUser(RegisterUserDto registerUserDto) {
        if (registerUserDto == null) {
            throw new InvalidParamException("User data is null");
        }
        User user = modelMapper.map(registerUserDto, User.class);
        user = userService.updateUser(user);
        return modelMapper.map(user, RegisterUserDto.class);
    }


    /**
     * Get user by id
     */
//    public UserDetails authenticate(LoginUserDto input) {
//        if (input == null) {
//            throw new InvalidParamException("User data cannot be null");
//        }
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        input.getEmailId(),
//                        input.getPassword()
//                )
//        );
//        User user = authenticationService.getUserData(input.getEmailId())
//                .orElseThrow(UserNotFoundException::new);
//
//        return modelMapper.map(user, UserDetails.class);
//    }
    public UserDetails authenticate(LoginUserDto input) {
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

        // Return UserDetails
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                new ArrayList<>()
        );
    }


}

