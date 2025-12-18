package com.gestion.stock.mapper;

import com.gestion.stock.dto.request.PermissionRequestDTO;
import com.gestion.stock.dto.response.PermissionResponseDTO;
import com.gestion.stock.entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toEntity(PermissionRequestDTO permissionRequestDTO);
    PermissionResponseDTO toResponseDto(Permission permission);
}
