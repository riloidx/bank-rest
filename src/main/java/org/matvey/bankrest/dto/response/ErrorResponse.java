package org.matvey.bankrest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * DTO для стандартного ответа об ошибке.
 * Содержит информацию об HTTP статусе, типе ошибки и сообщении.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO ответа при ошибке")
public class ErrorResponse {
    
    @Schema(description = "HTTP статус код", example = "400")
    private int status;
    
    @Schema(description = "Тип ошибки", example = "Bad Request")
    private String error;
    
    @Schema(description = "Сообщение об ошибке", example = "Некорректные данные")
    private String message;
    
    @Schema(description = "Время возникновения ошибки", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;
}
