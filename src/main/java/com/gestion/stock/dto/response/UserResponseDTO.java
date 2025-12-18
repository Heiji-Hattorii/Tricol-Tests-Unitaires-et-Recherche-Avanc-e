package com.gestion.stock.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponseDTO {
    private Long id;
    private String email;
    private LocalDateTime createdAt;
    private RoleResponseDTO role;
}
