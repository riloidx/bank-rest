package org.matvey.bankrest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.matvey.bankrest.dto.response.RoleResponseDto;
import org.matvey.bankrest.entity.Role;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleMapper {
    RoleResponseDto toDto(Role role);

    List<RoleResponseDto> toDto(List<Role> roles);

    Role toEntity(RoleResponseDto roleDto);

    default Set<String> mapRolesToStrings(Set<Role> roles) {
        if (roles == null) {
            return new HashSet<>();
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}
