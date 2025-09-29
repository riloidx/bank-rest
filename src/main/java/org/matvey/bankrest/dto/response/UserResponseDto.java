package org.matvey.bankrest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Schema(description = "DTO ответа с информацией о пользователе")
public class UserResponseDto {
    
    @Schema(description = "Уникальный идентификатор пользователя", example = "1")
    private Long id;
    
    @Schema(description = "Имя пользователя", example = "Иван Иванов")
    private String name;
    
    @Schema(description = "Email пользователя", example = "user@example.com")
    private String email;
    
    @Schema(description = "Роли пользователя", example = "[\"USER\"]")
    private Set<String> roles = new HashSet<>();
    
    @Schema(description = "Список карт пользователя")
    private List<CardResponseDto> cards = new ArrayList<>();
}
