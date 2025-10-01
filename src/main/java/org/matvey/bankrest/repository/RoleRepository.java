package org.matvey.bankrest.repository;

import org.matvey.bankrest.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностями Role.
 * Предоставляет методы для поиска ролей по различным критериям.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * Находит роль по названию.
     *
     * @param name название роли
     * @return Optional с ролью, если найдена
     */
    Optional<Role> findByName(String name);
}