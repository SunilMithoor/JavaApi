package com.app.repository;

import com.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository< User, Long> {


    /**
     * SQL query: SELECT * FROM users WHERE userId =
     *
     * @param id as String
     * @return User
     */
    User getUserById(Long id);

    boolean existsByEmailId(String emailId);

    boolean existsByMobileNo(String mobileNo);
}
