package org.matvey.bankrest.service;

import lombok.RequiredArgsConstructor;
import org.matvey.bankrest.dto.request.RegistrationDto;
import org.matvey.bankrest.dto.response.UserResponseDto;
import org.matvey.bankrest.entity.User;
import org.matvey.bankrest.exception.UserAlreadyExistsException;
import org.matvey.bankrest.exception.UserNotFoundException;
import org.matvey.bankrest.mapper.UserMapper;
import org.matvey.bankrest.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для управления пользователями.
 * Предоставляет функциональность для создания, поиска, обновления и удаления пользователей.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;


    /**
     * Находит пользователя по ID и возвращает DTO.
     *
     * @param id ID пользователя
     * @return DTO пользователя
     */
    public UserResponseDto findUserDtoById(long id) {
        User user = findUserById(id);
        return userMapper.toDto(user);
    }

    public UserResponseDto findUserDtoByEmail(String email) {
        User user = findUserByEmail(email);
        return userMapper.toDto(user);
    }

    public List<UserResponseDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toDto(users);
    }

    /**
     * Создает нового пользователя на основе данных регистрации.
     *
     * @param registrationDto данные для регистрации
     * @return созданный пользователь
     * @throws UserAlreadyExistsException если пользователь с таким email уже существует
     */
    public User create(RegistrationDto registrationDto) {
        validateEmailNotExists(registrationDto.getEmail());
        User user = prepareNewUser(registrationDto);
        userRepository.save(user);

        return user;
    }

    public UserResponseDto update(long id, RegistrationDto registrationDto) {
        User existingUser = findUserById(id);

        userMapper.updateEntityFromDto(registrationDto, existingUser);

        User updatedUser = userRepository.save(existingUser);

        return userMapper.toDto(updatedUser);
    }

    public void delete(long id) {
        findUserById(id);
        userRepository.deleteById(id);
    }

    /**
     * Подготавливает нового пользователя для сохранения.
     * Устанавливает роль USER и кодирует пароль.
     *
     * @param registrationDto данные регистрации
     * @return подготовленная сущность пользователя
     */
    private User prepareNewUser(RegistrationDto registrationDto) {
        User user = userMapper.toEntity(registrationDto);

        user.getRoles().add(roleService.findRoleByName("USER"));
        user.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));

        return user;
    }

    /**
     * Находит пользователя по ID.
     *
     * @param id ID пользователя
     * @return сущность пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
    public User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    /**
     * Находит пользователя по email.
     *
     * @param email email пользователя
     * @return сущность пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    private boolean isExistsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private void validateEmailNotExists(String email) {
        if (isExistsByEmail(email)) {
            throw new UserAlreadyExistsException(email);
        }
    }

}
