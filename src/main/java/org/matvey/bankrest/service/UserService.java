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

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;


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

    private User prepareNewUser(RegistrationDto registrationDto) {
        User user = userMapper.toEntity(registrationDto);

        user.getRoles().add(roleService.findRoleByName("USER"));
        user.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));

        return user;
    }

    public User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

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
