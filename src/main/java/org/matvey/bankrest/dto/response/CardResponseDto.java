package org.matvey.bankrest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.matvey.bankrest.entity.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "DTO ответа с информацией о карте")
public class CardResponseDto {
    
    @Schema(description = "Уникальный идентификатор карты", example = "1")
    private Long id;
    
    @Schema(description = "Замаскированный номер карты", example = "**** **** **** 1234")
    private String maskedCardNumber;
    
    @Schema(description = "Дата истечения срока действия карты", example = "2027-12-31")
    private LocalDate expirationDate;
    
    @Schema(description = "Статус карты", example = "ACTIVE")
    private CardStatus cardStatus;
    
    @Schema(description = "Баланс карты", example = "1500.75")
    private BigDecimal balance;
    
    @Schema(description = "Информация о владельце карты")
    private UserResponseDto owner;
}
