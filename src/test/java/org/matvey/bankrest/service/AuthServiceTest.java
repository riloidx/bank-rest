package org.matvey.bankrest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.matvey.bankrest.dto.request.LoginDto;
import org.matvey.bankrest.dto.request.RegistrationDto;
import org.matvey.bankrest.dto.response.AuthResponseDto;
import org.matvey.bankrest.dto.response.UserResponseDto;
import org.matvey.bankrest.entity.Role;
import org.matvey.bankrest.entity.User;
import org.matvey.bankrest.exception.UserAlreadyExistsException;
import org.matvey.bankrest.mapper.UserMapper;
import org.matvey.bankrest.repository.UserRepository;
import org.matvey.bankrest.security.JwtUtil;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegistrationDto registrationDto;
    private LoginDto loginDto;
    private User testUser;
    private UserResponseDto userResponseDto;
    private Role userRole;

    @BeforeEach
    void setUp() {
        registrationDto = new RegistrationDto();
        registrationDto.setName("Test User");
        registrationDto.setEmail("test@example.com");
        registrationDto.setPassword("Password123");

        loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("Password123");

        userRole = new Role();
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
    void register_WhenUserDoesNotExist_ShouldCreateUser() {
        // Given
        when(userRepository.existsByEmail(registrationDto.getEmail())).thenReturn(false);
        when(userMapper.toEntity(registrationDto)).thenReturn(testUser);
        when(passwordEncoder.encode(registrationDto.getPassword())).thenReturn("encodedPassword");
        when(roleService.findRoleByName("USER")).thenReturn(userRole);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(userResponseDto);
        when(jwtUtil.generateToken(testUser.getEmail())).thenReturn("jwt-token");

        // When
        AuthResponseDto result = authService.register(registrationDto);

        // Then
        assertNotNull(result);
        assertEquals(userResponseDto, result.getUser());
        assertEquals("jwt-token", result.getAccessToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_WhenUserAlreadyExists_ShouldThrowException() {
        // Given
        when(userRepository.existsByEmail(registrationDto.getEmail())).thenReturn(true);

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> authService.register(registrationDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_WhenCredentialsValid_ShouldReturnAuthResponse() {
        // Given
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(userResponseDto);
        when(jwtUtil.generateToken(testUser.getEmail())).thenReturn("jwt-token");

        // When
        AuthResponseDto result = authService.login(loginDto);

        // Then
        assertNotNull(result);
        assertEquals(userResponseDto, result.getUser());
        assertEquals("jwt-token", result.getAccessToken());
    }

    @Test
    void login_WhenCredentialsInvalid_ShouldThrowException() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        assertThrows(BadCredentialsException.class, () -> authService.login(loginDto));
    }
}