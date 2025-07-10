package com.app.service;

import com.app.config.LoggerService;
import com.app.entity.User;
import com.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.app.util.Utils.tagMethodName;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final LoggerService logger;

    @Autowired
    public UserServiceImpl(UserRepository repository, LoggerService logger) {
        this.repository = repository;
        this.logger = logger;
    }

    private static final String TAG = "UserService";

    /**
     * Get user by id
     *
     * @param id as String
     * @return User
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN','SUPER_ADMIN')")
    public User getUserById(Long id) {
        String methodName = "getUserById";
        try {
            logger.request(tagMethodName(TAG, methodName), "User id: " + id);
            return repository.getUserById(id);
        } catch (Exception e) {
            logger.error(tagMethodName(TAG, methodName), "Unable to get user data ", e);
            return null;
        }

    }

    @PreAuthorize("hasAnyRole('USER','ADMIN','SUPER_ADMIN')")
    public User getUserByEmailId(String emailId) {
        String methodName = "getUserByEmailId";
        logger.request(tagMethodName(TAG, methodName), "User email: " + emailId);
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
        String methodName = "isEmailExists";
        logger.request(tagMethodName(TAG, methodName), "User email: " + email);
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
        String methodName = "isMobileExists";
        logger.request(tagMethodName(TAG, methodName), "User mobileNo: " + mobileNo);
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
        String methodName = "saveUser";
        logger.request(tagMethodName(TAG, methodName), "Save User : " + user);
        return repository.save(user);
    }


    @PreAuthorize("hasAnyRole('USER','ADMIN','SUPER_ADMIN')")
    public User updateUser(User user) {
        String methodName = "updateUser";
        logger.request(tagMethodName(TAG, methodName), "Update User : " + user);
        return repository.save(user);
    }

    /**
     * Get users
     *
     * @return Users
     */
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    public List<User> getAllUsers() {
        String methodName = "updateUser";
        logger.request(tagMethodName(TAG, methodName), "Get all Users : ");
        return repository.findAll();
    }

}
