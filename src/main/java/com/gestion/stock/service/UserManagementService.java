package com.gestion.stock.service;

import com.gestion.stock.dto.request.UserPermissionRequestDTO;
import com.gestion.stock.dto.response.UserResponseDTO;

import java.util.List;

public interface UserManagementService {
    UserResponseDTO assignRoleToUser(Long userId, Long roleId);
    UserResponseDTO addPermissionToUser(UserPermissionRequestDTO request);
    UserResponseDTO removePermissionFromUser(Long userId, Long permissionId);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(Long id);
}