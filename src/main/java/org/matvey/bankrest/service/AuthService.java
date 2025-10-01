package org.matvey.bankrest.service;

import lombok.RequiredArgsConstructor;
import org.matvey.bankrest.dto.request.LoginDto;
import org.matvey.bankrest.dto.request.RegistrationDto;
import org.matvey.bankrest.dto.response.AuthResponseDto;
import org.matvey.bankrest.entity.User;
import org.matvey.bankrest.mapper.UserMapper;
import org.matvey.bankrest.security.CustomUserDetails;
import org.matvey.bankrest.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Сервис для обработки операций аутентификации.
 * Обеспечивает регистрацию новых пользователей и вход в систему.
 */
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;


    /**
     * Регистрирует нового пользователя в системе.
     *
     * @param registrationDto данные для регистрации
     * @return ответ с данными пользователя и JWT токеном
     */
    public AuthResponseDto register(RegistrationDto registrationDto) {
        User user = userService.create(registrationDto);
        String token = jwtUtil.generateToken(new CustomUserDetails(user));

        return new AuthResponseDto(userMapper.toDto(user), token);
    }

    /**
     * Выполняет аутентификацию пользователя.
     *
     * @param loginDto данные для входа
     * @return ответ с данными пользователя и JWT токеном
     */
    public AuthResponseDto login(LoginDto loginDto) {
        User user = userService.findUserByEmail(loginDto.getEmail());
        matchPasswordOrThrow(user.getPasswordHash(), loginDto.getPassword());

        String token = jwtUtil.generateToken(new CustomUserDetails(user));

        return new AuthResponseDto(userMapper.toDto(user), token);
    }

    /**
     * Проверяет соответствие пароля хешу.
     *
     * @param hashPassword хеш пароля из базы данных
     * @param password введенный пароль
     * @throws RuntimeException если пароли не совпадают
     */
    private void matchPasswordOrThrow(String hashPassword, String password) {
        if (!passwordEncoder.matches(password, hashPassword)) {
            throw new RuntimeException("Неверный пароль или почта");
        }
    }
}
