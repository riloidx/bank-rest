package org.matvey.bankrest.exception;

/**
 * Исключение, выбрасываемое при попытке выполнить недопустимую операцию с картой.
 * Например, при попытке активировать уже активную карту или заблокировать уже заблокированную.
 */
public class InvalidCardOperationException extends RuntimeException {
    /**
     * Создает исключение с указанным сообщением об ошибке.
     *
     * @param message описание недопустимой операции
     */
    public InvalidCardOperationException(String message) {
        super(message);
    }
}
