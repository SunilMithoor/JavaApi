package com.app.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserResponseData {

    @JsonProperty("jwt_token")
    private String jwtToken;

//    @JsonProperty("id")
//    private Long userId;

    @JsonProperty("expires_in")
    private Long expiresIn;


}
