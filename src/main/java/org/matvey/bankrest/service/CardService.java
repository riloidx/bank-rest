package org.matvey.bankrest.service;

import lombok.RequiredArgsConstructor;
import org.matvey.bankrest.dto.request.CardRequestDto;
import org.matvey.bankrest.dto.request.CardUpdateDto;
import org.matvey.bankrest.dto.request.TransferRequestDto;
import org.matvey.bankrest.dto.response.CardResponseDto;
import org.matvey.bankrest.entity.Card;
import org.matvey.bankrest.entity.CardStatus;
import org.matvey.bankrest.exception.CardNotFoundException;
import org.matvey.bankrest.exception.InsufficientFundsException;
import org.matvey.bankrest.exception.InvalidCardOperationException;
import org.matvey.bankrest.mapper.CardMapper;
import org.matvey.bankrest.repository.CardRepository;
import org.matvey.bankrest.util.CardUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepo;
    private final CardMapper cardMapper;
    private final UserService userService;
    private final CardUtils cardUtils;

    public List<CardResponseDto> findAllCards() {
        List<Card> cards = cardRepo.findAll();
        return cardMapper.toDto(cards);
    }

    public Page<CardResponseDto> findUserCards(Long userId, Pageable pageable) {
        Page<Card> cards = cardRepo.findByOwnerId(userId, pageable);
        return cards.map(cardMapper::toDto);
    }

    public Page<CardResponseDto> findUserCardsByStatus(Long userId, CardStatus status, Pageable pageable) {
        Page<Card> cards = cardRepo.findByOwnerIdAndCardStatus(userId, status, pageable);
        return cards.map(cardMapper::toDto);
    }

    public CardResponseDto findCardDtoById(long id) {
        return cardMapper.toDto(findCardById(id));
    }

    public CardResponseDto findUserCardById(Long userId, Long cardId) {
        Card card = cardRepo.findByIdAndOwnerId(cardId, userId)
                .orElseThrow(() -> new CardNotFoundException(cardId));
        return cardMapper.toDto(card);
    }

    @Transactional
    public CardResponseDto create(CardRequestDto cardRequestDto, long userId) {
        Card card = prepareNewCard(cardRequestDto, userId);
        Card savedCard = cardRepo.save(card);
        return cardMapper.toDto(savedCard);
    }

    @Transactional
    public CardResponseDto update(CardUpdateDto cardUpdateDto, long id) {
        Card existingCard = findCardById(id);
        cardMapper.updateEntityFromDto(cardUpdateDto, existingCard);
        Card updatedCard = cardRepo.save(existingCard);
        return cardMapper.toDto(updatedCard);
    }

    @Transactional
    public CardResponseDto blockCard(Long cardId, Long userId) {
        Card card = cardRepo.findByIdAndOwnerId(cardId, userId)
                .orElseThrow(() -> new CardNotFoundException(cardId));
        
        if (card.getCardStatus() == CardStatus.BLOCKED) {
            throw new InvalidCardOperationException("Карта уже заблокирована");
        }
        
        card.setCardStatus(CardStatus.BLOCKED);
        Card savedCard = cardRepo.save(card);
        return cardMapper.toDto(savedCard);
    }

    @Transactional
    public CardResponseDto activateCard(Long cardId) {
        Card card = findCardById(cardId);
        
        if (card.getCardStatus() == CardStatus.ACTIVE) {
            throw new InvalidCardOperationException("Карта уже активна");
        }
        
        if (card.getExpirationDate().isBefore(LocalDate.now())) {
            throw new InvalidCardOperationException("Нельзя активировать карту с истекшим сроком действия");
        }
        
        card.setCardStatus(CardStatus.ACTIVE);
        Card savedCard = cardRepo.save(card);
        return cardMapper.toDto(savedCard);
    }

    @Transactional
    public void deleteCard(Long cardId) {
        Card card = findCardById(cardId);
        cardRepo.delete(card);
    }

    @Transactional
    public void transferBetweenCards(TransferRequestDto transferRequest, Long userId) {
        Card fromCard = cardRepo.findByIdAndOwnerId(transferRequest.getFromCardId(), userId)
                .orElseThrow(() -> new CardNotFoundException(transferRequest.getFromCardId()));
        
        Card toCard = cardRepo.findByIdAndOwnerId(transferRequest.getToCardId(), userId)
                .orElseThrow(() -> new CardNotFoundException(transferRequest.getToCardId()));
        
        // Проверяем статус карт
        if (fromCard.getCardStatus() != CardStatus.ACTIVE) {
            throw new InvalidCardOperationException("Карта отправителя неактивна");
        }
        
        if (toCard.getCardStatus() != CardStatus.ACTIVE) {
            throw new InvalidCardOperationException("Карта получателя неактивна");
        }
        
        // Проверяем срок действия карт
        if (fromCard.getExpirationDate().isBefore(LocalDate.now())) {
            throw new InvalidCardOperationException("Срок действия карты отправителя истек");
        }
        
        if (toCard.getExpirationDate().isBefore(LocalDate.now())) {
            throw new InvalidCardOperationException("Срок действия карты получателя истек");
        }
        
        // Проверяем достаточность средств
        if (fromCard.getBalance().compareTo(transferRequest.getAmount()) < 0) {
            throw new InsufficientFundsException("Недостаточно средств на карте отправителя");
        }
        
        // Выполняем перевод
        fromCard.setBalance(fromCard.getBalance().subtract(transferRequest.getAmount()));
        toCard.setBalance(toCard.getBalance().add(transferRequest.getAmount()));
        
        cardRepo.save(fromCard);
        cardRepo.save(toCard);
    }

    private Card findCardById(long id) {
        return cardRepo.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
    }

    private Card prepareNewCard(CardRequestDto cardRequestDto, long userId) {
        Card card = cardMapper.toEntity(cardRequestDto);
        card.setOwner(userService.findUserById(userId));
        
        // Генерируем и шифруем номер карты
        String cardNumber = cardUtils.generateCardNumber();
        card.setCardNumber(cardUtils.encryptCardNumber(cardNumber));
        
        return card;
    }
}
