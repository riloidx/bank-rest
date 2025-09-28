package org.matvey.bankrest.service;

import lombok.RequiredArgsConstructor;
import org.matvey.bankrest.dto.request.CardRequestDto;
import org.matvey.bankrest.dto.request.CardUpdateDto;
import org.matvey.bankrest.dto.response.CardResponseDto;
import org.matvey.bankrest.entity.Card;
import org.matvey.bankrest.entity.User;
import org.matvey.bankrest.exception.CardNotFoundException;
import org.matvey.bankrest.mapper.CardMapper;
import org.matvey.bankrest.repository.CardRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepo;
    private final CardMapper cardMapper;
    private final UserService userService;


    public List<CardResponseDto> findAllCards() {
        List<Card> cards = cardRepo.findAll();

        return cardMapper.toDto(cards);
    }

    public CardResponseDto findCardDtoById(long id) {
        return cardMapper.toDto(findCardById(id));
    }

    public CardResponseDto create(CardRequestDto cardRequestDto, long userId) {
        Card card = prepareNewCard(cardRequestDto, userId);

        Card savedCard = cardRepo.save(card);

        return cardMapper.toDto(savedCard);
    }

    public CardResponseDto update(CardUpdateDto cardUpdateDto, long id) {
        Card existingCard = findCardById(id);

        cardMapper.updateEntityFromDto(cardUpdateDto, existingCard);

        Card updatedCard = cardRepo.save(existingCard);

        return cardMapper.toDto(updatedCard);
    }

    private Card findCardById(long id) {
        return cardRepo.findById(id).
                orElseThrow(() -> new CardNotFoundException(id));
    }

    private Card prepareNewCard(CardRequestDto cardRequestDto, long userId) {
        Card card = cardMapper.toEntity(cardRequestDto);
        card.setOwner(userService.findUserById(userId));

        return card;
    }
}
