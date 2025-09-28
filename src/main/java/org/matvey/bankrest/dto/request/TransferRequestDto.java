package org.matvey.bankrest.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequestDto {

    @NotNull(message = "ID карты отправителя не может быть пустым")
    private Long fromCardId;

    @NotNull(message = "ID карты получателя не может быть пустым")
    private Long toCardId;

    @NotNull(message = "Сумма перевода не может быть пустой")
    @Positive(message = "Сумма перевода должна быть положительной")
    @DecimalMin(value = "0.01", message = "Минимальная сумма перевода 0.01")
    private BigDecimal amount;

    private String description;
}

