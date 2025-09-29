package org.matvey.bankrest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.matvey.bankrest.dto.response.UserResponseDto;
import org.matvey.bankrest.entity.Role;
import org.matvey.bankrest.entity.User;
import org.matvey.bankrest.exception.UserNotFoundException;
import org.matvey.bankrest.mapper.UserMapper;
import org.matvey.bankrest.repository.UserRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserResponseDto userResponseDto;

    @BeforeEach
    void setUp() {
        Role userRole = new Role();
        userRole.setName("USER");

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRoles(Set.of(userRole));

        userResponseDto = new UserResponseDto();
        userResponseDto.setId(1L);
        userResponseDto.setName("Test User");
        userResponseDto.setEmail("test@example.com");
    }

    @Test
    void findUserById_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.findUserById(1L);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
    }

    @Test
    void findUserById_WhenUserNotExists_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findUserById(1L));
    }

    @Test
    void findUserDtoByEmail_WhenUserExists_ShouldReturnUserDto() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(userResponseDto);

        UserResponseDto result = userService.findUserDtoByEmail("test@example.com");

        assertNotNull(result);
        assertEquals(userResponseDto.getId(), result.getId());
        assertEquals(userResponseDto.getEmail(), result.getEmail());
    }

    @Test
    void findUserDtoByEmail_WhenUserNotExists_ShouldThrowException() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> 
            userService.findUserDtoByEmail("test@example.com"));
    }

    @Test
    void findAllUsers_ShouldReturnAllUsers() {
        User user2 = new User();
        user2.setId(2L);
        user2.setName("User 2");
        user2.setEmail("user2@example.com");

        UserResponseDto userDto2 = new UserResponseDto();
        userDto2.setId(2L);
        userDto2.setName("User 2");
        userDto2.setEmail("user2@example.com");

        List<User> users = Arrays.asList(testUser, user2);
        List<UserResponseDto> userDtos = Arrays.asList(userResponseDto, userDto2);

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDto(testUser)).thenReturn(userResponseDto);
        when(userMapper.toDto(user2)).thenReturn(userDto2);

        List<UserResponseDto> result = userService.findAllUsers();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(userResponseDto.getId(), result.get(0).getId());
        assertEquals(userDto2.getId(), result.get(1).getId());
    }

    @Test
    void existsByEmail_WhenUserExists_ShouldReturnTrue() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean result = userService.existsByEmail("test@example.com");

        assertTrue(result);
    }

    @Test
    void existsByEmail_WhenUserNotExists_ShouldReturnFalse() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);

        boolean result = userService.existsByEmail("test@example.com");

        assertFalse(result);
    }
}