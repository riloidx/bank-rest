package org.matvey.bankrest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO ответа с информацией о роли")
public class RoleResponseDto {
    
    @Schema(description = "Название роли", example = "USER")
    private String name;
}
