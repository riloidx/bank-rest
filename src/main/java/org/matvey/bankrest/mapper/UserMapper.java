package org.matvey.bankrest.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.matvey.bankrest.dto.request.RegistrationDto;
import org.matvey.bankrest.dto.response.UserResponseDto;
import org.matvey.bankrest.entity.User;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {CardMapper.class, RoleMapper.class})
public interface UserMapper {
    User toEntity(RegistrationDto registrationDto);

    UserResponseDto toDto(User user);

    List<UserResponseDto> toDto(List<User> users);

    void updateEntityFromDto(RegistrationDto registrationDto, @MappingTarget User existingUser);
}
