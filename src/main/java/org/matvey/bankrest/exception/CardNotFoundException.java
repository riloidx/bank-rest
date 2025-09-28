package org.matvey.bankrest.exception;

public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException(long id) {
        super("Карта с id " + id + " не найдена");
    }
}
