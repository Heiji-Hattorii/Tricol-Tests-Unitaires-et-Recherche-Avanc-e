package com.gestion.stock.dto.response;

import lombok.Data;

@Data
public class UserPermissionResponseDTO {
    private Long id;

    private UserResponseDTO user;
    private PermissionResponseDTO permission;
    private boolean active;
}
