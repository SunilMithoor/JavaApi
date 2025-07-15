package com.app.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserData {

    @JsonProperty("jwt_token")
    private String jwtToken;

    @JsonProperty("expires_in")
    private Long expiresIn;


}
