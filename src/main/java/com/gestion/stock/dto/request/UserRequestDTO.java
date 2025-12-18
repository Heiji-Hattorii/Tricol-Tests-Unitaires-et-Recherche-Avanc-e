package com.gestion.stock.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestDTO {

    @Email(message = "Email invalide")
    @NotBlank(message = "Email obligatoire")
    @Size(max = 100, message = "L'email ne peut pas dépasser 100 caracteres")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, max = 100, message = "Le mot de passe doit avoir entre 6 et 100 caracteres")
    private String password;
}
