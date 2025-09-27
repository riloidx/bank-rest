package org.matvey.bankrest.controller;

import lombok.RequiredArgsConstructor;
import org.matvey.bankrest.dto.response.UserResponseDto;
import org.matvey.bankrest.security.CustomUserDetails;
import org.matvey.bankrest.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> me(@AuthenticationPrincipal CustomUserDetails currentUser) {
        UserResponseDto dto = userService.findUserByEmail(currentUser.getUsername());

        return ResponseEntity.ok(dto);
    }
}
