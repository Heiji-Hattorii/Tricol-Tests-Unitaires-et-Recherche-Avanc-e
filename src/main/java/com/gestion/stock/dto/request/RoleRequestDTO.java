package com.gestion.stock.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
@Data
public class RoleRequestDTO {
    @NotBlank(message = "Le nom du role est obligatoire")
    @Size(max = 50, message = "Le nom du rôle ne peut pas depasser 50 caracteres")
    private String name;

    @NotEmpty(message = "Un role doit avoir au moins une permission")
    private List<Long> permissionIds;
}
