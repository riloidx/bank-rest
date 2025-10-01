package org.matvey.bankrest.exception;

/**
 * Исключение, выбрасываемое при попытке найти несуществующую роль.
 * Наследуется от RuntimeException для упрощения обработки.
 */
public class RoleNotFoundException extends RuntimeException {
    /**
     * Создает исключение с сообщением о ненайденной роли по ID.
     *
     * @param id идентификатор роли, которая не была найдена
     */
    public RoleNotFoundException(Long id) {
        super("Роль с id " + id + " не найдена");
    }

    /**
     * Создает исключение с сообщением о ненайденной роли по названию.
     *
     * @param name название роли, которая не была найдена
     */
    public RoleNotFoundException(String name) {
        super("Роль с именем " + name + " не найдена");
    }
}
