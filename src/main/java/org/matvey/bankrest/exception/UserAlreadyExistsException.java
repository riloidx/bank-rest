package org.matvey.bankrest.exception;

/**
 * Исключение, выбрасываемое при попытке создать пользователя с уже существующим email.
 * Наследуется от RuntimeException для упрощения обработки.
 */
public class UserAlreadyExistsException extends RuntimeException {
    /**
     * Создает исключение с сообщением о том, что пользователь с указанным email уже существует.
     *
     * @param email email пользователя, который уже существует в системе
     */
    public UserAlreadyExistsException(String email) {
        super("Пользователь с почтой " + email + " уже существует");
    }
}
