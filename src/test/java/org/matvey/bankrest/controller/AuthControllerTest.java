package org.matvey.bankrest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matvey.bankrest.dto.request.LoginDto;
import org.matvey.bankrest.dto.request.RegistrationDto;
import org.matvey.bankrest.dto.response.AuthResponseDto;
import org.matvey.bankrest.dto.response.UserResponseDto;
import org.matvey.bankrest.exception.UserAlreadyExistsException;
import org.matvey.bankrest.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private RegistrationDto registrationDto;
    private LoginDto loginDto;
    private AuthResponseDto authResponseDto;

    @BeforeEach
    void setUp() {
        registrationDto = new RegistrationDto();
        registrationDto.setName("Test User");
        registrationDto.setEmail("test@example.com");
        registrationDto.setPassword("Password123");

        loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setPassword("Password123");

        UserResponseDto userDto = new UserResponseDto();
        userDto.setId(1L);
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");

        authResponseDto = new AuthResponseDto(userDto, "jwt-token");
    }

    @Test
    void registration_WhenValidData_ShouldReturnAuthResponse() throws Exception {
        // Given
        when(authService.register(any(RegistrationDto.class))).thenReturn(authResponseDto);

        // When & Then
        mockMvc.perform(post("/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(1L))
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.accessToken").value("jwt-token"));
    }

    @Test
    void registration_WhenInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        registrationDto.setEmail("invalid-email");
        registrationDto.setPassword("123"); // Too short

        // When & Then
        mockMvc.perform(post("/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registration_WhenUserAlreadyExists_ShouldReturnConflict() throws Exception {
        // Given
        when(authService.register(any(RegistrationDto.class)))
                .thenThrow(new UserAlreadyExistsException("User already exists"));

        // When & Then
        mockMvc.perform(post("/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
                .andExpect(status().isConflict());
    }

    @Test
    void login_WhenValidCredentials_ShouldReturnAuthResponse() throws Exception {
        // Given
        when(authService.login(any(LoginDto.class))).thenReturn(authResponseDto);

        // When & Then
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(1L))
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andExpect(jsonPath("$.accessToken").value("jwt-token"));
    }

    @Test
    void login_WhenInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        // Given
        when(authService.login(any(LoginDto.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // When & Then
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_WhenInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        loginDto.setEmail("invalid-email");
        loginDto.setPassword(""); // Empty password

        // When & Then
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isBadRequest());
    }
}