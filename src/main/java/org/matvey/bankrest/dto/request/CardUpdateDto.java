package org.matvey.bankrest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import lombok.Data;
import org.matvey.bankrest.entity.CardStatus;

import java.time.LocalDate;

@Data
@Schema(description = "DTO для обновления карты")
public class CardUpdateDto {
    
    @Future(message = "Дата истечения срока действия должна быть в будущем")
    @Schema(description = "Новая дата истечения срока действия карты", example = "2027-12-31")
    private LocalDate expirationDate;
    
    @Schema(description = "Новый статус карты", example = "BLOCKED")
    private CardStatus cardStatus;
}
