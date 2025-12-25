package com.gestion.stock.service.impl;

import com.gestion.stock.dto.request.UserPermissionRequestDTO;
import com.gestion.stock.dto.response.UserResponseDTO;
import com.gestion.stock.entity.Permission;
import com.gestion.stock.entity.RoleApp;
import com.gestion.stock.entity.UserApp;
import com.gestion.stock.entity.UserPermission;
import com.gestion.stock.mapper.UserAppMapper;
import com.gestion.stock.repository.PermissionRepository;
import com.gestion.stock.repository.RoleAppRepository;
import com.gestion.stock.repository.UserAppRepository;
import com.gestion.stock.repository.UserPermissionRepository;
import com.gestion.stock.service.AuditService;
import com.gestion.stock.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class UserManagementServiceImpl implements UserManagementService {

    private final UserAppRepository userRepository;
    private final RoleAppRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final UserAppMapper userMapper;
    private final AuditService auditService;

    @Override
    public UserResponseDTO assignRoleToUser(Long userId, Long roleId) {
        UserApp user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        RoleApp role = roleRepository.findById(roleId)
            .orElseThrow(() -> new RuntimeException("Rôle non trouvé"));

        String oldRole = user.getRole() != null ? user.getRole().getName() : "AUCUN";
        user.setRole(role);
        user = userRepository.save(user);

        syncUserPermissionsWithRole(user);

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        auditService.logAction("ASSIGN_ROLE", "USER", userId.toString(), currentUser, 
            String.format("Rôle changé de %s à %s pour l'utilisateur %s", oldRole, role.getName(), user.getEmail()), null);

        return userMapper.toResponseDto(user);
    }

    private void syncUserPermissionsWithRole(UserApp user) {
        if (user.getRole() == null) {
            System.out.println("User has no role assigned");
            return;
        }

        System.out.println("Syncing permissions for user: " + user.getEmail() + " with role: " + user.getRole().getName());
        
        try {
            userPermissionRepository.deleteByUser(user);
            System.out.println("Deleted old permissions for user");

            Set<Permission> rolePermissions = user.getRole().getPermissions();
            System.out.println("Role has " + rolePermissions.size() + " permissions");
            
            int savedCount = 0;
            for (Permission permission : rolePermissions) {
                try {
                    UserPermission userPermission = new UserPermission();
                    userPermission.setUser(user);
                    userPermission.setPermission(permission);
                    userPermission.setActive(true);
                    
                    userPermissionRepository.save(userPermission);
                    savedCount++;
                    System.out.println("Saved permission: " + permission.getName() + " (" + savedCount + "/" + rolePermissions.size() + ")");
                } catch (Exception e) {
                    System.err.println("Error saving permission " + permission.getName() + ": " + e.getMessage());
                }
            }
            
            System.out.println("Successfully saved " + savedCount + " permissions for user " + user.getEmail());
            
        } catch (Exception e) {
            System.err.println("Error in syncUserPermissionsWithRole: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public UserResponseDTO addPermissionToUser(UserPermissionRequestDTO request) {
        UserApp user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        Permission permission = permissionRepository.findById(request.getPermissionId())
            .orElseThrow(() -> new RuntimeException("Permission non trouvée"));

        Optional<UserPermission> existingPermission = userPermissionRepository
            .findByUserAndPermission(user, permission);

        UserPermission userPermission;
        if (existingPermission.isPresent()) {
            userPermission = existingPermission.get();
            userPermission.setActive(request.getActive());
        } else {
            userPermission = new UserPermission();
            userPermission.setUser(user);
            userPermission.setPermission(permission);
            userPermission.setActive(request.getActive());
        }

        userPermissionRepository.save(userPermission);

        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        auditService.logAction("MODIFY_PERMISSION", "USER", user.getId().toString(), currentUser, 
            String.format("Permission %s %s pour l'utilisateur %s", 
                permission.getName(), 
                request.getActive() ? "accordée" : "révoquée", 
                user.getEmail()), null);

        UserApp updatedUser = userRepository.findByIdWithPermissions(user.getId())
            .orElse(userRepository.findById(user.getId()).get());
        return userMapper.toResponseDto(updatedUser);
    }

    @Override
    public UserResponseDTO removePermissionFromUser(Long userId, Long permissionId) {
        UserApp user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        Permission permission = permissionRepository.findById(permissionId)
            .orElseThrow(() -> new RuntimeException("Permission non trouvée"));

        Optional<UserPermission> userPermissionOpt = userPermissionRepository
            .findByUserAndPermission(user, permission);

        if (userPermissionOpt.isPresent()) {
            UserPermission userPermission = userPermissionOpt.get();
            userPermission.setActive(false);
            userPermissionRepository.save(userPermission);
            
            String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
            auditService.logAction("REMOVE_PERMISSION", "USER", userId.toString(), currentUser, 
                String.format("Permission %s désactivée pour l'utilisateur %s", permission.getName(), user.getEmail()), null);
        }

        UserApp updatedUser = userRepository.findByIdWithPermissions(userId)
            .orElse(userRepository.findById(userId).get());
        return userMapper.toResponseDto(updatedUser);
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
            .map(userMapper::toResponseDto)
            .toList();
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        UserApp user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return userMapper.toResponseDto(user);
    }
}