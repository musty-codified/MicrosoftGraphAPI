package com.mustycodified.MiddlewareAuthWithGraphAPI.model;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RoleDto {
    private String id;
    private String name;
    private List<PermissionDto> permissions;

    public RoleDto(String id, String name, List<PermissionDto> permissions) {
        this.id = id;
        this.name = name;
        this.permissions = permissions;
    }
}
