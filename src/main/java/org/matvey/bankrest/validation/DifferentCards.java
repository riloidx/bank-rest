package org.matvey.bankrest.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Аннотация для валидации того, что карты отправителя и получателя различны.
 * Применяется на уровне класса для проверки полей fromCardId и toCardId.
 */
@Documented
@Constraint(validatedBy = DifferentCardsValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DifferentCards {
    String message() default "Карта отправителя и получателя должны быть разными";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}