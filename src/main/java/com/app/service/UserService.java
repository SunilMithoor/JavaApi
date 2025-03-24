package com.app.service;

import com.app.entity.User;
import com.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    /**
     * Get user by id
     *
     * @param id as String
     * @return User
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN','SUPER_ADMIN')")
    public User getUserById(Long id) {
        return repository.getUserById(id);
    }

    @PreAuthorize("hasAnyRole('USER','ADMIN','SUPER_ADMIN')")
    public User getUserByEmailId(String emailId) {
        return repository.getUserByEmailId(emailId);
    }

    /**
     * Check email exits
     *
     * @param email as String
     * @return true/false
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN','SUPER_ADMIN')")
    public boolean isEmailExists(String email) {
        return repository.existsByEmailId(email);
    }

    /**
     * Check mobileNo exits
     *
     * @param mobileNo as String
     * @return true/false
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN','SUPER_ADMIN')")
    public boolean isMobileExists(String mobileNo) {
        return repository.existsByMobileNo(mobileNo);
    }

    /**
     * Save user
     *
     * @param user as User
     * @return User
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN','SUPER_ADMIN')")
    public User saveUser(User user) {
        return repository.save(user);
    }


    @PreAuthorize("hasAnyRole('USER','ADMIN','SUPER_ADMIN')")
    public User updateUser(User user) {
        return repository.save(user);
    }

    public boolean checkPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }


    /**
     * Get users
     *
     * @return Users
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN','SUPER_ADMIN')")
    public List<User> getAllUsers() {
        return repository.findAll();
    }

}
