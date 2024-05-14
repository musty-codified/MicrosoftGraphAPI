package com.mustycodified.MiddlewareAuthWithGraphAPI.model;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private long id;
    private String name;
    private String email;
    private String phoneNumber;
    private RoleDto role;
    private boolean active;
    private String token;
    private String refreshToken;

    public User(long id, String name, String email, String phoneNumber,
                RoleDto role, boolean active, String token, String refreshToken) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.active = active;
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
