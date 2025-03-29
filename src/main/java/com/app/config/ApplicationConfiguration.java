package com.app.config;


import com.app.repository.UserRepository;
import com.app.util.MessageConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static com.app.util.Utils.tagMethodName;


@Configuration
@EnableWebSecurity
public class ApplicationConfiguration {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoggerService logger;

    private static final String TAG = "ApplicationConfiguration";


    @Bean
    @Transactional(readOnly = true)
    UserDetailsService userDetailsService() {
        return loginId -> {
            String methodName = "userDetailsService";
//            logger.info(tagMethodName(TAG, methodName), "Attempting to load user by loginId: " + loginId);
            logger.info(tagMethodName(TAG, methodName), "Attempting to load user by loginId: ");

            return userRepository.findByUserName(loginId)
                    .or(() -> userRepository.findByEmailId(loginId))
                    .or(() -> userRepository.findByMobileNo(loginId))
                    .map(user -> {
//                        logger.info(tagMethodName(TAG, methodName), "User found: " + loginId);
                        logger.info(tagMethodName(TAG, methodName), "User found: ");
                        return (UserDetails) user;
                    })
                    .orElseThrow(() -> {
                        logger.warn(tagMethodName(TAG, methodName), "User not found: ");
                        return new UsernameNotFoundException(MessageConstants.USER_NOT_FOUND);
                    });
        };
    }


    @Bean
    public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter() {
        logger.info(tagMethodName(TAG, "byteArrayHttpMessageConverter"), "Initializing ByteArrayHttpMessageConverter");
        return new ByteArrayHttpMessageConverter();
    }


    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        logger.info(tagMethodName(TAG, "passwordEncoder"), "Initializing BCryptPasswordEncoder");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        String methodName = "authenticationManager";
        logger.info(tagMethodName(TAG, methodName), "Initializing AuthenticationManager");
        return config.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        String methodName = "authenticationProvider";
        logger.info(tagMethodName(TAG, methodName), "Initializing AuthenticationProvider");

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        logger.info(tagMethodName(TAG, methodName), "AuthenticationProvider configured successfully");
        return authProvider;
    }


}
