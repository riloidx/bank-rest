package org.matvey.bankrest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.matvey.bankrest.entity.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "DTO для создания новой карты")
public class CardRequestDto {
    
    @NotNull(message = "Дата истечения срока действия не может быть пустой")
    @Future(message = "Дата истечения срока действия должна быть в будущем")
    @Schema(description = "Дата истечения срока действия карты", example = "2027-12-31")
    private LocalDate expirationDate;
    
    @Schema(description = "Статус карты", example = "ACTIVE", defaultValue = "ACTIVE")
    private CardStatus cardStatus = CardStatus.ACTIVE;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Баланс не может быть отрицательным")
    @Schema(description = "Начальный баланс карты", example = "1000.00", defaultValue = "0.00")
    private BigDecimal balance = BigDecimal.ZERO;
}
