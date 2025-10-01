package org.matvey.bankrest.exception;

/**
 * Исключение, выбрасываемое при попытке найти несуществующего пользователя.
 * Наследуется от RuntimeException для упрощения обработки.
 */
public class UserNotFoundException extends RuntimeException {
    /**
     * Создает исключение с сообщением о ненайденном пользователе по ID.
     *
     * @param id идентификатор пользователя, который не был найден
     */
    public UserNotFoundException(Long id) {
      super("Пользователь с id " + id + " не найден");
    }

    /**
     * Создает исключение с сообщением о ненайденном пользователе по email.
     *
     * @param email email пользователя, который не был найден
     */
    public UserNotFoundException(String email) {
      super("Пользователь с почтой " + email + " не найден");
    }
}
