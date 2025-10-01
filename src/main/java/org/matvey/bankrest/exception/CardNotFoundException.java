package org.matvey.bankrest.exception;

/**
 * Исключение, выбрасываемое при попытке найти несуществующую карту.
 * Наследуется от RuntimeException для упрощения обработки.
 */
public class CardNotFoundException extends RuntimeException {
    /**
     * Создает исключение с сообщением о ненайденной карте.
     *
     * @param id идентификатор карты, которая не была найдена
     */
    public CardNotFoundException(long id) {
        super("Карта с id " + id + " не найдена");
    }
}
