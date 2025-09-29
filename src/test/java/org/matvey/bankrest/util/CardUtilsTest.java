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
        String cardNumber = cardUtils.generateCardNumber();

        assertNotNull(cardNumber);
        assertEquals(16, cardNumber.length());
        assertTrue(cardNumber.matches("\\d{16}"));
    }

    @Test
    void generateCardNumber_ShouldGenerateUniqueNumbers() {
        String cardNumber1 = cardUtils.generateCardNumber();
        String cardNumber2 = cardUtils.generateCardNumber();

        assertNotEquals(cardNumber1, cardNumber2);
    }

    @Test
    void maskCardNumber_ShouldMaskCorrectly() {
        String cardNumber = "1234567890123456";

        String maskedNumber = cardUtils.maskCardNumber(cardNumber);

        assertEquals("**** **** **** 3456", maskedNumber);
    }

    @Test
    void maskCardNumber_WhenNullInput_ShouldReturnNull() {
        String maskedNumber = cardUtils.maskCardNumber(null);

        assertNull(maskedNumber);
    }

    @Test
    void maskCardNumber_WhenShortInput_ShouldHandleGracefully() {
        String shortCardNumber = "123456";

        String maskedNumber = cardUtils.maskCardNumber(shortCardNumber);

        assertNotNull(maskedNumber);
        assertTrue(maskedNumber.contains("*"));
    }

    @Test
    void encryptCardNumber_ShouldEncryptCardNumber() {
        String cardNumber = "1234567890123456";

        String encryptedNumber = cardUtils.encryptCardNumber(cardNumber);

        assertNotNull(encryptedNumber);
        assertNotEquals(cardNumber, encryptedNumber);
    }

    @Test
    void decryptCardNumber_ShouldDecryptToOriginal() {
        String originalCardNumber = "1234567890123456";
        String encryptedNumber = cardUtils.encryptCardNumber(originalCardNumber);

        String decryptedNumber = cardUtils.decryptCardNumber(encryptedNumber);

        assertEquals(originalCardNumber, decryptedNumber);
    }

    @Test
    void encryptDecrypt_ShouldBeReversible() {
        String originalCardNumber = "9876543210987654";

        String encrypted = cardUtils.encryptCardNumber(originalCardNumber);
        String decrypted = cardUtils.decryptCardNumber(encrypted);

        assertEquals(originalCardNumber, decrypted);
        assertNotEquals(originalCardNumber, encrypted);
    }
}