package org.matvey.bankrest.controller;

import lombok.RequiredArgsConstructor;
import org.matvey.bankrest.dto.request.LoginDto;
import org.matvey.bankrest.dto.request.RegistrationDto;
import org.matvey.bankrest.dto.response.AuthResponseDto;
import org.matvey.bankrest.dto.response.UserResponseDto;
import org.matvey.bankrest.entity.User;
import org.matvey.bankrest.mapper.UserMapper;
import org.matvey.bankrest.security.CustomUserDetails;
import org.matvey.bankrest.security.JwtUtil;
import org.matvey.bankrest.service.AuthService;
import org.matvey.bankrest.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    @PostMapping("/registration")
    public ResponseEntity<AuthResponseDto> registration(@RequestBody RegistrationDto registrationDto) {
        AuthResponseDto response = authService.register(registrationDto);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto) {
        AuthResponseDto response = authService.login(loginDto);

        return ResponseEntity.ok(response);
    }
}
