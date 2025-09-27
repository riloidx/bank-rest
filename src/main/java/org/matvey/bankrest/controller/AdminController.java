package org.matvey.bankrest.controller;

import lombok.RequiredArgsConstructor;
import org.matvey.bankrest.dto.response.UserResponseDto;
import org.matvey.bankrest.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<?> findAll() {
        List<UserResponseDto> users = userService.findAllUsers();

        return ResponseEntity.ok(users);
    }

}
