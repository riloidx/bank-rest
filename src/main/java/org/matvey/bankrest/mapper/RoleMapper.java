package org.matvey.bankrest.mapper;

import org.mapstruct.Mapper;
import org.matvey.bankrest.dto.response.RoleResponseDto;
import org.matvey.bankrest.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponseDto toDto(Role role);
}
