package org.matvey.bankrest.service;

import lombok.RequiredArgsConstructor;
import org.matvey.bankrest.dto.response.RoleResponseDto;
import org.matvey.bankrest.entity.Role;
import org.matvey.bankrest.exception.RoleNotFoundException;
import org.matvey.bankrest.mapper.RoleMapper;
import org.matvey.bankrest.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * Сервис для управления ролями пользователей.
 * Предоставляет функциональность для поиска и получения ролей в системе.
 */
@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepo;
    private final RoleMapper roleMapper;

    /**
     * Получает список всех ролей в системе.
     *
     * @return список DTO ролей
     */
    public List<RoleResponseDto> findAllRoles() {
        List<Role> roles = roleRepo.findAll();

        return roleMapper.toDto(roles);
    }

    /**
     * Находит роль по названию.
     *
     * @param roleName название роли
     * @return сущность роли
     * @throws RoleNotFoundException если роль не найдена
     */
    public Role findRoleByName(String roleName) {
        return findRoleByNameOrThrow(roleName);
    }

    /**
     * Находит роль по названию или выбрасывает исключение.
     *
     * @param roleName название роли
     * @return сущность роли
     * @throws RoleNotFoundException если роль не найдена
     */
    private Role findRoleByNameOrThrow(String roleName) {
        return roleRepo.findByName(roleName).
                orElseThrow(() -> new RoleNotFoundException(roleName));
    }

}
