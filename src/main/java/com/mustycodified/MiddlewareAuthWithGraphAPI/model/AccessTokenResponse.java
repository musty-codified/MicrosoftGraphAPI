package com.mustycodified.MiddlewareAuthWithGraphAPI.model;


import lombok.Data;

@Data
public class AccessTokenResponse {
    private String token_type;
    private int expires_in;
    private int ext_expires_in;
    private String access_token;
}
