package org.matvey.bankrest.security;

import lombok.RequiredArgsConstructor;
import org.matvey.bankrest.entity.User;
import org.matvey.bankrest.exception.UserNotFoundException;
import org.matvey.bankrest.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Сервис для загрузки пользовательских данных для Spring Security.
 * Реализует интерфейс UserDetailsService для аутентификации.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    /**
     * Загружает пользователя по email для аутентификации.
     *
     * @param email email пользователя
     * @return UserDetails с информацией о пользователе
     * @throws UserNotFoundException если пользователь не найден
     */
    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        return new CustomUserDetails(user);
    }
}
