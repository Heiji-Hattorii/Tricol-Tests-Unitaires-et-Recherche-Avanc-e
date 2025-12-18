package com.gestion.stock.service;

import com.gestion.stock.dto.request.RoleRequestDTO;
import com.gestion.stock.dto.response.RoleResponseDTO;

import java.util.List;

public interface RoleService {

    RoleResponseDTO createRole(RoleRequestDTO roleRequestDTO);
    RoleResponseDTO getRoleById(Long id);
    List<RoleResponseDTO> getAllRoles();
    RoleResponseDTO updateRole(Long id, RoleRequestDTO roleRequestDTO);
    String deleteRole(Long id);
}
