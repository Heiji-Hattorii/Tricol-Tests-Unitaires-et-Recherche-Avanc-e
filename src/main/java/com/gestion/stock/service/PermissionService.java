package com.gestion.stock.service;

import com.gestion.stock.dto.request.PermissionRequestDTO;
import com.gestion.stock.dto.response.PermissionResponseDTO;

import java.util.List;

public interface PermissionService {

    PermissionResponseDTO createPermission(PermissionRequestDTO dto);
    PermissionResponseDTO getPermissionById(Long id);
    List<PermissionResponseDTO> getAllPermissions();
    PermissionResponseDTO updatePermission(Long id, PermissionRequestDTO dto);
    String deletePermission(Long id);
}
