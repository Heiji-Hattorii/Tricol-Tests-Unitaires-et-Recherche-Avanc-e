package com.gestion.stock.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PermissionRequestDTO {
    @NotBlank(message = "Le nom de la permission est obligatoire")
    @Size(max = 50, message = "Le nom ne peut pas dépasser 50 caracteres")
    private String name;
}
