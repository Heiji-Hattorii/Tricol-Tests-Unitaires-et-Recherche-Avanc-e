package com.gestion.stock.controller;

import com.gestion.stock.dto.request.UserPermissionRequestDTO;
import com.gestion.stock.dto.response.UserResponseDTO;
import com.gestion.stock.service.UserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserManagementService userManagementService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userManagementService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userManagementService.getUserById(id));
    }

    @PutMapping("/{userId}/role/{roleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> assignRoleToUser(
            @PathVariable Long userId, 
            @PathVariable Long roleId) {
        return ResponseEntity.ok(userManagementService.assignRoleToUser(userId, roleId));
    }

    @PostMapping("/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> modifyUserPermission(
            @Valid @RequestBody UserPermissionRequestDTO request) {
        return ResponseEntity.ok(userManagementService.addPermissionToUser(request));
    }

    @DeleteMapping("/{userId}/permissions/{permissionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDTO> removePermissionFromUser(
            @PathVariable Long userId, 
            @PathVariable Long permissionId) {
        return ResponseEntity.ok(userManagementService.removePermissionFromUser(userId, permissionId));
    }
}