package org.matvey.bankrest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "DTO для обновления данных пользователя")
public class UserUpdateDto {
    
    @NotBlank(message = "Имя не может быть пустым")
    @Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 50 символов")
    @Pattern(regexp = "^[a-zA-Zа-яА-Я\\s]+$", message = "Имя может содержать только буквы и пробелы")
    @Schema(description = "Новое имя пользователя", example = "Петр Петров")
    private String name;
}
