package org.matvey.bankrest.entity;

/**
 * Перечисление типов транзакций.
 * Определяет различные виды операций в банковской системе.
 */
public enum TransactionType {
    /** Перевод средств между картами */
    TRANSFER,
    /** Пополнение счета */
    DEPOSIT,
    /** Снятие средств */
    WITHDRAWAL,
    /** Создание новой карты */
    CARD_CREATION,
    /** Блокировка карты */
    CARD_BLOCK,
    /** Активация карты */
    CARD_ACTIVATION
}