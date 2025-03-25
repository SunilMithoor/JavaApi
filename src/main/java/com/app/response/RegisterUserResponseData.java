package com.app.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserResponseData {
    @JsonProperty("jwt_token")
    private String jwtToken;
    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("username")
    private String username;
    @JsonProperty("email_id")
    private String emailId;
    @JsonProperty("country_code")
    private String countryCode;
    @JsonProperty("mobile_no")
    private String mobileNo;
    @JsonProperty("role")
    private String role;
}
