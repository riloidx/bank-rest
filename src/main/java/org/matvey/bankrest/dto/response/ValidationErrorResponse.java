package org.matvey.bankrest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "DTO ответа при ошибке валидации")
public class ValidationErrorResponse extends ErrorResponse {
    
    @Schema(description = "Ошибки валидации полей", 
            example = "{\"email\": \"Некорректный формат email\", \"password\": \"Пароль слишком короткий\"}")
    private Map<String, String> fieldErrors;
}
