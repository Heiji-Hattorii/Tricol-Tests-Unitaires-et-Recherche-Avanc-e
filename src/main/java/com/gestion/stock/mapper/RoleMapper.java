package com.gestion.stock.mapper;

import com.gestion.stock.dto.request.RoleRequestDTO;
import com.gestion.stock.dto.response.RoleResponseDTO;
import com.gestion.stock.entity.RoleApp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleApp toEntity(RoleRequestDTO dto);
    @Mapping(target = "permissions", source = "permissions")
    RoleResponseDTO toResponseDto(RoleApp Role);
}
