package com.app.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

import static com.app.util.MessageConstants.*;


@Getter
@Setter
@ToString
public class RegisterUserDto {

    @JsonProperty("id")
    private Long id;

    @NotBlank(message = FIRST_NAME_REQUIRED)
    @Size(min = 3, max = 50, message = FIRST_NAME_LENGTH)
    @JsonProperty("first_name")
    private String firstName;

    @Size(max = 50, message = LAST_NAME_MAX_LENGTH)
    @JsonProperty("last_name")
    private String lastName;

    @Size(min = 5, max = 50, message = USER_NAME_LENGTH)
    @JsonProperty("username")
    private String username;

    @Email(message = EMAIL_INVALID_FORMAT)
    @Size(max = 50, message = EMAIL_MAX_LENGTH)
    @JsonProperty("email_id")
    private String emailId;

    @JsonProperty("is_email_id_verified")
    private Boolean isEmailIdVerified = false;

    @NotBlank(message = COUNTRY_CODE_REQUIRED)
    @Size(min = 1, max = 5, message = COUNTRY_CODE_MAX_LENGTH_5)
    @JsonProperty("country_code")
    private String countryCode = "91";

    @NotBlank(message = MOBILE_NUMBER_REQUIRED)
    @Size(min = 10, max = 15, message = MOBILE_NUMBER_MAX_LENGTH)
    @Pattern(regexp = "\\d{10,15}", message = MOBILE_NUMBER_INVALID_FORMAT)
    @JsonProperty("mobile_no")
    private String mobileNo;

    @JsonProperty("is_mobile_no_verified")
    private Boolean isMobileNoVerified = false;

    @Size(max = 255, message = PASSWORD_HASH_MAX_LENGTH)
    @JsonProperty("password")
    private String password;

    @Size(max = 255, message = PASSWORD_HASH_MAX_LENGTH)
    @JsonProperty("password_hash")
    private String passwordHash;

    @PastOrPresent(message = DATE_OF_BIRTH_INVALID)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonProperty("date_of_birth")
    private Date dateOfBirth;


    @NotNull(message = ROLE_REQUIRED)
    @Pattern(regexp = "USER|SUPER_ADMIN|ADMIN|SELLER", message = ROLE_INVALID)
    @JsonProperty("role")
    private String role = "USER";

    @JsonProperty("is_active")
    private Boolean isActive = true;
}

