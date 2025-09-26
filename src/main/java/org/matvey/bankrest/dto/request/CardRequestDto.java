package org.matvey.bankrest.dto.request;

import lombok.Data;
import org.matvey.bankrest.entity.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardRequestDto {
    private Long id;
    private String cardNumber;
    private LocalDate expirationDate;
    private CardStatus cardStatus;
    private BigDecimal balance;
}
