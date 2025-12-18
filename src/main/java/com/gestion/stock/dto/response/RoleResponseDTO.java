package com.gestion.stock.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class RoleResponseDTO {
    private Long id;
    private String name;
    private List<PermissionResponseDTO> permissions;
}
