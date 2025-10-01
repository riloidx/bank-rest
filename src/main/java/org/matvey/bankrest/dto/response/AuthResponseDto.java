package org.matvey.bankrest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO ответа при успешной аутентификации.
 * Содержит информацию о пользователе и JWT токен для доступа к API.
 */
@Data
@AllArgsConstructor
@Schema(description = "DTO ответа при аутентификации")
public class AuthResponseDto {
    
    @Schema(description = "Информация о пользователе")
    private UserResponseDto user;
    
    @Schema(description = "JWT токен доступа", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;
}
