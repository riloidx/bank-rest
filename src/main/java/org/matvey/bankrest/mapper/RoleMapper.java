package org.matvey.bankrest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.matvey.bankrest.dto.response.RoleResponseDto;
import org.matvey.bankrest.entity.Role;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper для преобразования между сущностями Role и DTO.
 * Использует MapStruct для автоматической генерации кода маппинга.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleMapper {
    RoleResponseDto toDto(Role role);

    List<RoleResponseDto> toDto(List<Role> roles);

    Role toEntity(RoleResponseDto roleDto);

    /**
     * Преобразует набор ролей в набор строковых названий ролей.
     *
     * @param roles набор сущностей ролей
     * @return набор названий ролей
     */
    default Set<String> mapRolesToStrings(Set<Role> roles) {
        if (roles == null) {
            return new HashSet<>();
        }
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}
