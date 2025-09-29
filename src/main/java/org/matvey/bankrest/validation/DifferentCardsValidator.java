package org.matvey.bankrest.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.matvey.bankrest.dto.request.TransferRequestDto;

public class DifferentCardsValidator implements ConstraintValidator<DifferentCards, TransferRequestDto> {

    @Override
    public void initialize(DifferentCards constraintAnnotation) {
        // Инициализация не требуется
    }

    @Override
    public boolean isValid(TransferRequestDto transferRequest, ConstraintValidatorContext context) {
        if (transferRequest == null) {
            return true;
        }

        if (transferRequest.getFromCardId() == null || transferRequest.getToCardId() == null) {
            return true; // Пусть другие валидаторы обработают null значения
        }

        return !transferRequest.getFromCardId().equals(transferRequest.getToCardId());
    }
}