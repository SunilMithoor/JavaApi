package com.app.service;

import com.app.config.LoggerService;
import com.app.entity.User;
import com.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.app.util.Utils.tagMethodName;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoggerService logger;

    private final String TAG = "AuthenticationService";


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