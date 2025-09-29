package org.matvey.bankrest.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CardUtilsTest {

    private CardUtils cardUtils;

    @BeforeEach
    void setUp() {
        cardUtils = new CardUtils();
    }

    @Test
    void generateCardNumber_ShouldGenerateValidCardNumber() {
        // When
        String cardNumber = cardUtils.generateCardNumber();

        // Then
        assertNotNull(cardNumber);
        assertEquals(16, cardNumber.length());
        assertTrue(cardNumber.matches("\\d{16}"));
    }

    @Test
    void generateCardNumber_ShouldGenerateUniqueNumbers() {
        // When
        String cardNumber1 = cardUtils.generateCardNumber();
        String cardNumber2 = cardUtils.generateCardNumber();

        // Then
        assertNotEquals(cardNumber1, cardNumber2);
    }

    @Test
    void maskCardNumber_ShouldMaskCorrectly() {
        // Given
        String cardNumber = "1234567890123456";

        // When
        String maskedNumber = cardUtils.maskCardNumber(cardNumber);

        // Then
        assertEquals("**** **** **** 3456", maskedNumber);
    }

    @Test
    void maskCardNumber_WhenNullInput_ShouldReturnNull() {
        // When
        String maskedNumber = cardUtils.maskCardNumber(null);

        // Then
        assertNull(maskedNumber);
    }

    @Test
    void maskCardNumber_WhenShortInput_ShouldHandleGracefully() {
        // Given
        String shortCardNumber = "123456";

        // When
        String maskedNumber = cardUtils.maskCardNumber(shortCardNumber);

        // Then
        assertNotNull(maskedNumber);
        assertTrue(maskedNumber.contains("*"));
    }

    @Test
    void encryptCardNumber_ShouldEncryptCardNumber() {
        // Given
        String cardNumber = "1234567890123456";

        // When
        String encryptedNumber = cardUtils.encryptCardNumber(cardNumber);

        // Then
        assertNotNull(encryptedNumber);
        assertNotEquals(cardNumber, encryptedNumber);
    }

    @Test
    void decryptCardNumber_ShouldDecryptToOriginal() {
        // Given
        String originalCardNumber = "1234567890123456";
        String encryptedNumber = cardUtils.encryptCardNumber(originalCardNumber);

        // When
        String decryptedNumber = cardUtils.decryptCardNumber(encryptedNumber);

        // Then
        assertEquals(originalCardNumber, decryptedNumber);
    }

    @Test
    void encryptDecrypt_ShouldBeReversible() {
        // Given
        String originalCardNumber = "9876543210987654";

        // When
        String encrypted = cardUtils.encryptCardNumber(originalCardNumber);
        String decrypted = cardUtils.decryptCardNumber(encrypted);

        // Then
        assertEquals(originalCardNumber, decrypted);
        assertNotEquals(originalCardNumber, encrypted);
    }
}