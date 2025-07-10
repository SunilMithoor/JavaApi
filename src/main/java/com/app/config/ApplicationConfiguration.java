package com.app.config;


import com.app.service.UserDetailsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static com.app.util.Utils.tagMethodName;


@Configuration
@EnableWebSecurity
public class ApplicationConfiguration {

    private final LoggerService logger;
    private static final String TAG = "ApplicationConfiguration";


    @Autowired
    public ApplicationConfiguration(LoggerService logger) {
        this.logger = logger;
    }

    @Bean
    UserDetailsService userDetailsService(UserDetailsLoader loader) {
        return loader::loadUserByLoginId;
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
    AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        String methodName = "authenticationProvider";
        logger.info(tagMethodName(TAG, methodName), "Initializing AuthenticationProvider");

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        logger.info(tagMethodName(TAG, methodName), "AuthenticationProvider configured successfully");
        return authProvider;
    }


}
