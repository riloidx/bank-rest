package org.matvey.bankrest.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.matvey.bankrest.dto.request.TransferRequestDto;

/**
 * Валидатор для проверки того, что карты отправителя и получателя различны.
 * Реализует логику валидации для аннотации @DifferentCards.
 */
public class DifferentCardsValidator implements ConstraintValidator<DifferentCards, TransferRequestDto> {

    @Override
    public void initialize(DifferentCards constraintAnnotation) {
    }

    /**
     * Проверяет, что ID карт отправителя и получателя различны.
     *
     * @param transferRequest объект запроса перевода
     * @param context контекст валидации
     * @return true, если карты различны или одно из значений null
     */
    @Override
    public boolean isValid(TransferRequestDto transferRequest, ConstraintValidatorContext context) {
        if (transferRequest == null) {
            return true;
        }

        if (transferRequest.getFromCardId() == null || transferRequest.getToCardId() == null) {
            return true;
        }

        return !transferRequest.getFromCardId().equals(transferRequest.getToCardId());
    }
}