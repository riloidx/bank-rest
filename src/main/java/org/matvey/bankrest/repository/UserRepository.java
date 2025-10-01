package org.matvey.bankrest.repository;

import org.matvey.bankrest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностями User.
 * Предоставляет методы для поиска пользователей по различным критериям.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Находит пользователя по email адресу.
     *
     * @param email email адрес пользователя
     * @return Optional с пользователем, если найден
     */
    Optional<User> findByEmail(String email);
}