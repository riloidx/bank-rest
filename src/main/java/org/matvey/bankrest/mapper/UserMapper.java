package org.matvey.bankrest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.matvey.bankrest.dto.request.RegistrationDto;
import org.matvey.bankrest.dto.response.UserResponseDto;
import org.matvey.bankrest.entity.User;

import java.util.List;

/**
 * Mapper для преобразования между сущностями User и DTO.
 * Использует MapStruct для автоматической генерации кода маппинга.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = { RoleMapper.class})
public interface UserMapper {
    /**
     * Преобразует DTO регистрации в сущность User.
     *
     * @param registrationDto DTO с данными регистрации
     * @return сущность User
     */
    User toEntity(RegistrationDto registrationDto);

    /**
     * Преобразует сущность User в DTO ответа.
     *
     * @param user сущность пользователя
     * @return DTO ответа с информацией о пользователе
     */
    UserResponseDto toDto(User user);

    List<UserResponseDto> toDto(List<User> users);

    void updateEntityFromDto(RegistrationDto registrationDto, @MappingTarget User existingUser);
}
