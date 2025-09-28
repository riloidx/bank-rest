package org.matvey.bankrest.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.matvey.bankrest.entity.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardRequestDto {
    
    @NotNull(message = "Дата истечения срока действия не может быть пустой")
    @Future(message = "Дата истечения срока действия должна быть в будущем")
    private LocalDate expirationDate;
    
    private CardStatus cardStatus = CardStatus.ACTIVE;
    
    private BigDecimal balance = BigDecimal.ZERO;
}
