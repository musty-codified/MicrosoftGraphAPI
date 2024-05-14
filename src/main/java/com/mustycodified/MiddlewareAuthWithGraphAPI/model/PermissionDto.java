package com.mustycodified.MiddlewareAuthWithGraphAPI.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PermissionDto {
    private String id;
    private String name;
    private String description;

    public PermissionDto(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
