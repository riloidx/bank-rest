package org.matvey.bankrest.exception;

/**
 * Исключение, выбрасываемое при недостатке средств на карте для выполнения операции.
 * Наследуется от RuntimeException для упрощения обработки.
 */
public class InsufficientFundsException extends RuntimeException {
    /**
     * Создает исключение с указанным сообщением об ошибке.
     *
     * @param message описание ошибки недостатка средств
     */
    public InsufficientFundsException(String message) {
        super(message);
    }
}
