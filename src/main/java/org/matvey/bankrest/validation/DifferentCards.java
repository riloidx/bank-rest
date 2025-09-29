package org.matvey.bankrest.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DifferentCardsValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DifferentCards {
    String message() default "Карта отправителя и получателя должны быть разными";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}