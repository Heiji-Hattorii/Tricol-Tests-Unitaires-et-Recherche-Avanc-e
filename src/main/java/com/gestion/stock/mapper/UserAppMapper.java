package com.gestion.stock.mapper;

import com.gestion.stock.dto.request.UserRequestDTO;
import com.gestion.stock.dto.response.UserResponseDTO;
import com.gestion.stock.entity.UserApp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserAppMapper {
    @Mapping(target = "role", ignore = true)
    UserApp toEntity(UserRequestDTO dto);
    UserResponseDTO toResponseDto(UserApp user);
}
