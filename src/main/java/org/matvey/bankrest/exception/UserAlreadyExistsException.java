package org.matvey.bankrest.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super("Пользователь с почтой " + email + " уже существует");
    }
}
