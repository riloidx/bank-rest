package org.matvey.bankrest.dto.request;

import org.matvey.bankrest.entity.CardStatus;

import java.time.LocalDate;

public class CardUpdateDto {
    LocalDate expirationDate;
    CardStatus cardStatus;
}
