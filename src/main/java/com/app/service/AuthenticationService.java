package com.app.service;

import com.app.entity.User;
import java.util.Optional;


public interface AuthenticationService {

    Optional<User> getUserData(String emailId);

    Optional<User> findByEmailIdOrMobileNoOrUserName(String input);

}