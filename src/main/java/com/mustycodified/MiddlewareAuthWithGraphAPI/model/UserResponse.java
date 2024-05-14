package com.mustycodified.MiddlewareAuthWithGraphAPI.model;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {

    private boolean success;
    private String internalCode;
    private String message;
    private User user;

    private boolean active;

    private String token;
    private String refreshToken;


}
