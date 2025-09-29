package org.matvey.bankrest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.matvey.bankrest.validation.DifferentCards;

import java.math.BigDecimal;

@Data
@Schema(description = "DTO для перевода средств между картами")
@DifferentCards
public class TransferRequestDto {

    @NotNull(message = "ID карты отправителя не может быть пустым")
    @Schema(description = "ID карты, с которой осуществляется перевод", example = "1")
    private Long fromCardId;

    @NotNull(message = "ID карты получателя не может быть пустым")
    @Schema(description = "ID карты, на которую осуществляется перевод", example = "2")
    private Long toCardId;

    @NotNull(message = "Сумма перевода не может быть пустой")
    @Positive(message = "Сумма перевода должна быть положительной")
    @DecimalMin(value = "0.01", message = "Минимальная сумма перевода 0.01")
    @Schema(description = "Сумма перевода", example = "100.50")
    private BigDecimal amount;

    @Size(max = 255, message = "Описание не может превышать 255 символов")
    @Schema(description = "Описание перевода", example = "Перевод на карту для покупок")
    private String description;
}

