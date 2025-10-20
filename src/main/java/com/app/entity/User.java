package com.app.entity;

import com.app.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.app.util.MessageConstants.DATE_OF_BIRTH_INVALID;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    @JsonProperty("first_name")
    private String firstName;

    @Column(name = "last_name", length = 50)
    @JsonProperty("last_name")
    private String lastName;

    @Column(name = "email_id", unique = true, length = 50)
    @JsonProperty("email_id")
    private String emailId;

    @Column(name = "is_email_id_verified")
    @JsonProperty("is_email_id_verified")
    private Boolean isEmailIdVerified = false;

    @Column(name = "country_code", nullable = false, length = 5)
    @JsonProperty("country_code")
    private String countryCode = "91";

    @Column(name = "mobile_no", nullable = false, unique = true, length = 15)
    @JsonProperty("mobile_no")
    private String mobileNo;

    @Column(name = "is_mobile_no_verified")
    @JsonProperty("is_mobile_no_verified")
    private Boolean isMobileNoVerified = false;

    @Column(name = "password_hash", length = 255)
    @JsonProperty("password_hash")
    private String passwordHash;

    @Column(name = "date_of_birth")
    @Temporal(TemporalType.DATE)
    @PastOrPresent(message = DATE_OF_BIRTH_INVALID)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    @JsonProperty("role")
    private Role role = Role.USER;

    @Column(name = "is_active")
    @JsonProperty("is_active")
    private Boolean isActive = true;

    @Column(name = "is_locked")
    @JsonProperty("is_locked")
    private Boolean isLocked = false;

    @Column(name = "username", nullable = false, unique = true)
    @JsonProperty("username")
    private String userName;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty("created_at")
    private Date createdAt = new Date();

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonProperty("updated_at")
    private Date updatedAt = new Date();

    public void setPassword(String password) {
        this.passwordHash = passwordEncoder().encode(password);
    }

    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}


