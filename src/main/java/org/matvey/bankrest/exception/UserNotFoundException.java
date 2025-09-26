package org.matvey.bankrest.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
      super("Пользователь с id " + id + " не найден");
    }

    public UserNotFoundException(String email) {
      super("Пользователь с почтой " + email + " не найден");
    }
}
