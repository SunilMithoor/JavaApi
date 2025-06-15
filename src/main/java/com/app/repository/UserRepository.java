package com.app.repository;

import com.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


    /**
     * SQL query: SELECT * FROM users WHERE userId =
     *
     * @param id as String
     * @return User
     */
    User getUserById(Long id);

    User getUserByEmailId(String emailId);

    User getUserByMobileNo(String mobileNo);

    boolean existsByEmailId(String emailId);

    boolean existsByMobileNo(String mobileNo);

    Optional<User> findByEmailId(String emailId);

    Optional<User> findByUserName(String username);

    Optional<User> findByMobileNo(String mobileNo);

    Optional<User> findByEmailIdOrMobileNoOrUserName(String emailId, String mobileNo, String username);


}
