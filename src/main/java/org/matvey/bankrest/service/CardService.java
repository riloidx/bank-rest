package org.matvey.bankrest.service;

import lombok.RequiredArgsConstructor;
import org.matvey.bankrest.repository.CardRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepo;




}
