package com.app.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import static com.app.util.MessageConstants.PASSWORD_MAX_LENGTH;


@Getter
@Setter
@ToString
@Data
public class LoginUser {

    @Size(min = 5, max = 50, message = "Login ID must be between 5-50 characters")
    @JsonProperty("login_id")
    private String loginId;

    @Size(max = 255, message = PASSWORD_MAX_LENGTH)
    @JsonProperty("password")
    private String password;

}