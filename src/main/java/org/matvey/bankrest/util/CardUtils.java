package org.matvey.bankrest.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Утилитный класс для работы с банковскими картами.
 * Предоставляет методы для генерации, шифрования, маскирования и валидации номеров карт.
 */
@Component
public class CardUtils {

    @Value("${security.encryption.algorithm}")
    private String algorithm;
    @Value("${security.encryption.transformation}")
    private String transformation;
    @Value("${security.encryption.secret-key}")
    private String secretKey;

    /**
     * Генерирует случайный 16-значный номер карты.
     *
     * @return сгенерированный номер карты
     */
    public String generateCardNumber() {
        SecureRandom random = new SecureRandom();
        StringBuilder cardNumber = new StringBuilder();

        for (int i = 0; i < 16; i++) {
            cardNumber.append(random.nextInt(10));
        }

        return cardNumber.toString();
    }

    /**
     * Шифрует номер карты для безопасного хранения.
     *
     * @param cardNumber номер карты для шифрования
     * @return зашифрованный номер карты в Base64
     * @throws RuntimeException при ошибке шифрования
     */
    public String encryptCardNumber(String cardNumber) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(this.secretKey.getBytes(StandardCharsets.UTF_8), algorithm);
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encryptedBytes = cipher.doFinal(cardNumber.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при шифровании номера карты", e);
        }
    }

    /**
     * Расшифровывает номер карты.
     *
     * @param encryptedCardNumber зашифрованный номер карты
     * @return расшифрованный номер карты
     * @throws RuntimeException при ошибке расшифровки
     */
    public String decryptCardNumber(String encryptedCardNumber) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(this.secretKey.getBytes(StandardCharsets.UTF_8), algorithm);
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] decodedBytes = Base64.getDecoder().decode(encryptedCardNumber);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при расшифровке номера карты", e);
        }
    }

    /**
     * Маскирует номер карты, показывая только последние 4 цифры.
     *
     * @param cardNumber номер карты для маскирования
     * @return замаскированный номер карты
     */
    public String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }

        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + lastFour;
    }

    /**
     * Проверяет валидность номера карты по алгоритму Луна.
     *
     * @param cardNumber номер карты для проверки
     * @return true, если номер карты валиден
     */
    public boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 16) {
            return false;
        }

        int sum = 0;
        boolean alternate = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit = (digit % 10) + 1;
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        return (sum % 10) == 0;
    }
}
