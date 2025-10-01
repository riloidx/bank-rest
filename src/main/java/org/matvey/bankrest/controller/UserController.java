package org.matvey.bankrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.matvey.bankrest.dto.response.ErrorResponse;
import org.matvey.bankrest.dto.response.UserResponseDto;
import org.matvey.bankrest.security.CustomUserDetails;
import org.matvey.bankrest.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для работы с профилем пользователя.
 * Предоставляет API для получения информации о текущем пользователе.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Пользователи", description = "API для работы с профилем пользователя")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Получить информацию о текущем пользователе", 
               description = "Возвращает информацию о профиле текущего авторизованного пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Информация о пользователе успешно получена",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = UserResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Не авторизован",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ErrorResponse.class)))
    })
    /**
     * Получает информацию о текущем авторизованном пользователе.
     *
     * @param currentUser данные текущего пользователя
     * @return информация о пользователе
     */
    public ResponseEntity<UserResponseDto> me(@AuthenticationPrincipal CustomUserDetails currentUser) {
        UserResponseDto dto = userService.findUserDtoByEmail(currentUser.getUsername());
        return ResponseEntity.ok(dto);
    }
}
