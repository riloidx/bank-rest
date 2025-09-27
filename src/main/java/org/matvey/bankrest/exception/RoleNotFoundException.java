package org.matvey.bankrest.exception;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(Long id) {
        super("Роль с id " + id + " не найдена");
    }

    public RoleNotFoundException(String name) {
        super("Роль с именем " + name + " не найдена");
    }
}
