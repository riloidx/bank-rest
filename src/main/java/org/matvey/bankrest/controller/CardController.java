package org.matvey.bankrest.controller;

import lombok.RequiredArgsConstructor;
import org.matvey.bankrest.service.CardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;


}
