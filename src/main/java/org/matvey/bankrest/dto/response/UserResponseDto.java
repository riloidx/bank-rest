package org.matvey.bankrest.dto.response;

import lombok.Data;
import org.matvey.bankrest.entity.Role;

import java.util.List;
import java.util.Set;

@Data
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private Set<Role> roles;
    private List<CardResponseDto> cards;
}
