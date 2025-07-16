package com.app.service;

import com.app.config.LoggerService;
import com.app.entity.User;
import com.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.app.util.Utils.tagMethodName;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final LoggerService logger;

    private static final String TAG = "AuthenticationService";

    @Autowired
    public AuthenticationServiceImpl(UserRepository userRepository, LoggerService logger) {
        this.userRepository = userRepository;
        this.logger = logger;
    }

    public Optional<User> getUserData(String emailId) {
        String methodName = "getUserData";
        try {
            logger.request(tagMethodName(TAG, methodName), "User emailId: " + emailId);
            return userRepository.findByEmailId(emailId);
        } catch (Exception e) {
            logger.error(tagMethodName(TAG, methodName), "Unable to get user data ", e);
            return Optional.empty();
        }
    }

    public Optional<User> findByEmailIdOrMobileNoOrUserName(String input) {
        String methodName = "findByEmailIdOrMobileNoOrUserName";
        try {
            logger.request(tagMethodName(TAG, methodName), "User findByEmailIdOrMobileNoOrUserName: " + input);
            return userRepository.findByEmailIdOrMobileNoOrUserName(input, input, input);
        } catch (Exception e) {
            logger.error(tagMethodName(TAG, methodName), "Unable to get user data ", e);
            return Optional.empty();
        }
    }
}
