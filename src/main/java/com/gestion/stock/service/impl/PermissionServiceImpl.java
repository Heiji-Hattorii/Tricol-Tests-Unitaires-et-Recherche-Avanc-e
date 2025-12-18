package com.gestion.stock.service.impl;

import com.gestion.stock.dto.request.PermissionRequestDTO;
import com.gestion.stock.dto.response.PermissionResponseDTO;
import com.gestion.stock.entity.Permission;
import com.gestion.stock.mapper.PermissionMapper;
import com.gestion.stock.repository.PermissionRepository;
import com.gestion.stock.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Override
    public PermissionResponseDTO createPermission(PermissionRequestDTO dto) {
        if (permissionRepository.existsByName(dto.getName())) {throw new RuntimeException("La permission existe deka");}
        return permissionMapper.toResponseDto(permissionRepository.save(permissionMapper.toEntity(dto)));
    }

    @Override
    public PermissionResponseDTO getPermissionById(Long id) {
        return permissionMapper.toResponseDto(permissionRepository.findById(id).orElseThrow(() -> new RuntimeException("Permission introuvable")));
    }

    @Override
    public List<PermissionResponseDTO> getAllPermissions() {
        return permissionRepository.findAll().stream().map(permissionMapper::toResponseDto).toList();
    }

    @Override
    public PermissionResponseDTO updatePermission(Long id, PermissionRequestDTO dto) {
        Permission permission = permissionRepository.findById(id).orElseThrow(() -> new RuntimeException("Permission introuvable"));
        permission.setName(dto.getName());
        return permissionMapper.toResponseDto(permissionRepository.save(permission));
    }

    @Override
    public String deletePermission(Long id) {
        permissionRepository.delete(permissionRepository.findById(id).orElseThrow(() -> new RuntimeException("Permission introuvable")));
        return "Permission supprimee";
    }
}
