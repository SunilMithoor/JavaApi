package com.app.service;

import com.app.entity.User;
import com.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepository repository;

    /**
     * Get user by id
     *
     * @param id as String
     * @return User
     */
    public User getUserById( Long id) {
        return repository.getUserById(id);
    }

    /**
     * Check email exits
     *
     * @param email as String
     * @return true/false
     */
    public boolean isEmailExists(String email) {
        return repository.existsByEmailId(email);
    }

    /**
     * Check mobileNo exits
     *
     * @param mobileNo as String
     * @return true/false
     */
    public boolean isMobileExists(String mobileNo) {
        return repository.existsByMobileNo(mobileNo);
    }

    /**
     * Save user
     *
     * @param user as User
     * @return User
     */
    public User saveUser( User user) {
        return repository.save(user);
    }

    /**
     * Get users
     *
     * @return Users
     */
    public List<User> getAllUsers() {
        return repository.findAll();
    }


}
