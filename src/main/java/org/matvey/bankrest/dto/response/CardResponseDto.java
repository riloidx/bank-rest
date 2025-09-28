package org.matvey.bankrest.dto.response;

import lombok.Data;
import org.matvey.bankrest.entity.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardResponseDto {
    private Long id;
    private String maskedCardNumber;
    private LocalDate expirationDate;
    private CardStatus cardStatus;
    private BigDecimal balance;
    private UserResponseDto owner;
}
