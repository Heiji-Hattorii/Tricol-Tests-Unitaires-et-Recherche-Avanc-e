package com.gestion.stock.mapper;

import com.gestion.stock.dto.request.UserRequestDTO;
import com.gestion.stock.dto.response.UserResponseDTO;
import com.gestion.stock.entity.UserApp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserAppMapper {
    @Mapping(target = "role", ignore = true)
    UserApp toEntity(UserRequestDTO dto);
    
    @Mapping(target = "effectivePermissions", expression = "java(getEffectivePermissions(user))")
    UserResponseDTO toResponseDto(UserApp user);
    
    default Set<String> getEffectivePermissions(UserApp user) {
        Set<String> permissions = new HashSet<>();
        user.getUserPermissions().stream()
            .filter(up -> up.isActive())
            .forEach(up -> permissions.add(up.getPermission().getName()));
        return permissions;
    }
}
