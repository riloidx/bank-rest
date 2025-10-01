package org.matvey.bankrest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Главный класс Spring Boot приложения для банковской системы.
 * Запускает REST API для управления банковскими картами и пользователями.
 */
@SpringBootApplication
public class BankRestApplication {

    /**
     * Точка входа в приложение.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        SpringApplication.run(BankRestApplication.class, args);
    }

}
