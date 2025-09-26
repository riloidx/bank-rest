package org.matvey.bankrest.service;

import lombok.RequiredArgsConstructor;
import org.matvey.bankrest.dto.request.RegistrationDto;
import org.matvey.bankrest.dto.response.UserResponseDto;
import org.matvey.bankrest.entity.User;
import org.matvey.bankrest.exception.UserNotFoundException;
import org.matvey.bankrest.mapper.UserMapper;
import org.matvey.bankrest.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponseDto findUserById(long id) {
        User user = findByIdOrThrow(id);
        return userMapper.toDto(user);
    }

    public UserResponseDto findUserByEmail(String email) {
        User user = findByEmailOrThrow(email);
        return userMapper.toDto(user);
    }

    public List<UserResponseDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toDto(users);
    }

    public UserResponseDto create(RegistrationDto registrationDto) {
    }


    private User findByIdOrThrow(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private User findByEmailOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

}
