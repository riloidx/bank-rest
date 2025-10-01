package org.matvey.bankrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.matvey.bankrest.dto.request.LoginDto;
import org.matvey.bankrest.dto.request.RegistrationDto;
import org.matvey.bankrest.dto.response.AuthResponseDto;
import org.matvey.bankrest.dto.response.ErrorResponse;
import org.matvey.bankrest.dto.response.ValidationErrorResponse;
import org.matvey.bankrest.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для обработки запросов аутентификации.
 * Предоставляет API для регистрации новых пользователей и входа в систему.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Аутентификация", description = "API для регистрации и входа в систему")
public class AuthController {
    private final AuthService authService;

    /**
     * Регистрирует нового пользователя в системе.
     *
     * @param registrationDto данные для регистрации пользователя
     * @return ответ с данными пользователя и JWT токеном
     */
    @PostMapping("/registration")
    @Operation(summary = "Регистрация нового пользователя", 
               description = "Создает нового пользователя в системе и возвращает JWT токен")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = AuthResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Пользователь с таким email уже существует",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AuthResponseDto> registration(@Valid @RequestBody RegistrationDto registrationDto) {
        AuthResponseDto response = authService.register(registrationDto);
        return ResponseEntity.ok(response);
    }

    /**
     * Выполняет аутентификацию пользователя.
     *
     * @param loginDto данные для входа в систему
     * @return ответ с данными пользователя и JWT токеном
     */
    @PostMapping("/login")
    @Operation(summary = "Вход в систему", 
               description = "Аутентифицирует пользователя и возвращает JWT токен")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешная аутентификация",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = AuthResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Неверные учетные данные",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginDto loginDto) {
        AuthResponseDto response = authService.login(loginDto);
        return ResponseEntity.ok(response);
    }
}
