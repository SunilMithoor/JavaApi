package com.app.service;

import com.app.entity.User;
import com.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;


    public Optional<User> getUserData(String emailId) {
        return userRepository.findByEmailId(emailId);
    }

    public Optional<User> findByEmailIdOrMobileNoOrUserName(String input) {
        return userRepository.findByEmailIdOrMobileNoOrUserName(input, input, input);
    }


}