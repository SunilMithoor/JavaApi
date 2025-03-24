package com.app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.app.util.MessageConstants.PASSWORD_MAX_LENGTH;


@Getter
@Setter
@ToString
public class LoginUserDto {

//    @Email(message = EMAIL_INVALID_FORMAT)
//    @Size(max = 50, message = EMAIL_MAX_LENGTH)
//    @JsonProperty("email_id")
//    private String emailId;

//    @Size(min = 5, max = 50, message = USER_NAME_LENGTH)
//    @JsonProperty("username")
//    private String username;

//    @Size(min = 10, max = 15, message = MOBILE_NUMBER_MAX_LENGTH)
//    @Pattern(regexp = "\\d{10,15}", message = MOBILE_NUMBER_INVALID_FORMAT)
//    @JsonProperty("mobile_no")
//    private String mobileNo;

    @Size(min = 5, max = 50, message = "Login ID must be between 5-50 characters")
    @JsonProperty("login_id")
    private String loginId;

    @Size(max = 255, message = PASSWORD_MAX_LENGTH)
    @JsonProperty("password")
    private String password;

//    @Size(min = 6, max = 10, message = OTP_LENGTH)
//    @JsonProperty("otp")
//    private String otp;

}