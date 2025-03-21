package com.app.entity;

import com.app.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "email_id", unique = true, length = 100)
    private String emailId;

    @Column(name = "is_email_id_verified")
    private Boolean isEmailIdVerified = false;

    @Column(name = "country_code", nullable = false, length = 5)
    private String countryCode = "+91";

    @Column(name = "mobile_no", nullable = false, unique = true, length = 15)
    private String mobileNo;

    @Column(name = "is_mobile_no_verified")
    private Boolean isMobileNoVerified = false;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "jwt_token", columnDefinition = "TEXT")
    private String jwtToken;

    @Column(name = "date_of_birth", length = 10)
    private String dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role = UserRole.CUSTOMER;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt = new Date();
}


