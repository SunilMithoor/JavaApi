package com.app.service;

import com.app.entity.User;

import java.util.List;


public interface UserService {

    User getUserById(Long id);

    User getUserByEmailId(String emailId);

    boolean isEmailExists(String email);

    boolean isMobileExists(String mobileNo);

    User saveUser(User user);

    User updateUser(User user);

    List<User> getAllUsers();
}
