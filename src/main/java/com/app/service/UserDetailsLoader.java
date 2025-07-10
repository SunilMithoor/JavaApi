package com.app.service;

import com.app.config.LoggerService;
import com.app.repository.UserRepository;
import com.app.util.MessageConstants;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.app.util.Utils.tagMethodName;


@Service
public class UserDetailsLoader {

    private final UserRepository userRepository;
    private final LoggerService logger;
    private static final String TAG = "UserDetailsLoader";

    public UserDetailsLoader(UserRepository userRepository, LoggerService logger) {
        this.userRepository = userRepository;
        this.logger = logger;
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserByLoginId(String loginId) {
        String methodName = "loadUserByLoginId";
        logger.info(tagMethodName(TAG, methodName), "Attempting to load user by loginId: ");
        return userRepository.findByUserName(loginId)
                .or(() -> userRepository.findByEmailId(loginId))
                .or(() -> userRepository.findByMobileNo(loginId))
                .map(user -> {
                    logger.info(tagMethodName(TAG, methodName), "User found: ");
                    return (UserDetails) user;
                })
                .orElseThrow(() -> {
                    logger.warn(tagMethodName(TAG, methodName), "User not found: ");
                    return new UsernameNotFoundException(MessageConstants.USER_NOT_FOUND);
                });
    }
}
