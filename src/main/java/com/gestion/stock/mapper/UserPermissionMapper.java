package com.gestion.stock.mapper;

import com.gestion.stock.dto.request.UserPermissionRequestDTO;
import com.gestion.stock.dto.response.UserPermissionResponseDTO;
import com.gestion.stock.entity.UserPermission;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserAppMapper.class, PermissionMapper.class})
public interface UserPermissionMapper {
    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "permission.id", source = "permissionId")
    UserPermission toEntity(UserPermissionRequestDTO userPermissionRequestDTO);
    UserPermissionResponseDTO toResponseDto(UserPermission userPermission);
}
