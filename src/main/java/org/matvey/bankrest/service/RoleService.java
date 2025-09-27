package org.matvey.bankrest.service;

import lombok.RequiredArgsConstructor;
import org.matvey.bankrest.dto.response.RoleResponseDto;
import org.matvey.bankrest.entity.Role;
import org.matvey.bankrest.exception.RoleNotFoundException;
import org.matvey.bankrest.mapper.RoleMapper;
import org.matvey.bankrest.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepo;
    private final RoleMapper roleMapper;

    public List<RoleResponseDto> findAllRoles() {
        List<Role> roles = roleRepo.findAll();

        return roleMapper.toDto(roles);
    }

    public Role findRoleByName(String roleName) {
        return findRoleByNameOrThrow(roleName);
    }

    private Role findRoleByNameOrThrow(String roleName) {
        return roleRepo.findByName(roleName).
                orElseThrow(() -> new RoleNotFoundException(roleName));
    }

}
