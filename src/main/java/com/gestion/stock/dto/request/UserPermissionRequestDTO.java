package com.gestion.stock.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserPermissionRequestDTO {

    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private Long userId;

    @NotNull(message = "L'ID de la permission est obligatoire")
    private Long permissionId;
    private Boolean active = true;
}
