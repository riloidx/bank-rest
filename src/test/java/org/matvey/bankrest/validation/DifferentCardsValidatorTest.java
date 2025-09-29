package org.matvey.bankrest.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matvey.bankrest.dto.request.TransferRequestDto;

import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class DifferentCardsValidatorTest {

    private DifferentCardsValidator validator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new DifferentCardsValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Test
    void isValid_WhenCardsAreDifferent_ShouldReturnTrue() {
        // Given
        TransferRequestDto transferRequest = new TransferRequestDto();
        transferRequest.setFromCardId(1L);
        transferRequest.setToCardId(2L);
        transferRequest.setAmount(BigDecimal.valueOf(100));

        // When
        boolean result = validator.isValid(transferRequest, context);

        // Then
        assertTrue(result);
    }

    @Test
    void isValid_WhenCardsAreSame_ShouldReturnFalse() {
        // Given
        TransferRequestDto transferRequest = new TransferRequestDto();
        transferRequest.setFromCardId(1L);
        transferRequest.setToCardId(1L);
        transferRequest.setAmount(BigDecimal.valueOf(100));

        // When
        boolean result = validator.isValid(transferRequest, context);

        // Then
        assertFalse(result);
    }

    @Test
    void isValid_WhenTransferRequestIsNull_ShouldReturnTrue() {
        // When
        boolean result = validator.isValid(null, context);

        // Then
        assertTrue(result);
    }

    @Test
    void isValid_WhenFromCardIdIsNull_ShouldReturnTrue() {
        // Given
        TransferRequestDto transferRequest = new TransferRequestDto();
        transferRequest.setFromCardId(null);
        transferRequest.setToCardId(2L);
        transferRequest.setAmount(BigDecimal.valueOf(100));

        // When
        boolean result = validator.isValid(transferRequest, context);

        // Then
        assertTrue(result);
    }

    @Test
    void isValid_WhenToCardIdIsNull_ShouldReturnTrue() {
        // Given
        TransferRequestDto transferRequest = new TransferRequestDto();
        transferRequest.setFromCardId(1L);
        transferRequest.setToCardId(null);
        transferRequest.setAmount(BigDecimal.valueOf(100));

        // When
        boolean result = validator.isValid(transferRequest, context);

        // Then
        assertTrue(result);
    }

    @Test
    void isValid_WhenBothCardIdsAreNull_ShouldReturnTrue() {
        // Given
        TransferRequestDto transferRequest = new TransferRequestDto();
        transferRequest.setFromCardId(null);
        transferRequest.setToCardId(null);
        transferRequest.setAmount(BigDecimal.valueOf(100));

        // When
        boolean result = validator.isValid(transferRequest, context);

        // Then
        assertTrue(result);
    }
}