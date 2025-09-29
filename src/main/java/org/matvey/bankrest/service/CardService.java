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

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final UserService userService;
    private final CardUtils cardUtils;

    @Transactional(readOnly = true)
    public List<CardResponseDto> findAllCards() {
        return cardMapper.toDto(cardRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Page<CardResponseDto> findUserCards(Long userId, Pageable pageable) {
        return cardRepository.findByOwnerId(userId, pageable).map(cardMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<CardResponseDto> findUserCardsByStatus(Long userId, CardStatus status, Pageable pageable) {
        return cardRepository.findByOwnerIdAndCardStatus(userId, status, pageable).map(cardMapper::toDto);
    }

    @Transactional(readOnly = true)
    public CardResponseDto findCardDtoById(long id) {
        return cardMapper.toDto(getCardById(id));
    }

    @Transactional(readOnly = true)
    public CardResponseDto findUserCardById(Long userId, Long cardId) {
        Card card = cardRepository.findByIdAndOwnerId(cardId, userId)
                .orElseThrow(() -> new CardNotFoundException(cardId));
        return cardMapper.toDto(card);
    }

    @Transactional
    public CardResponseDto create(CardRequestDto dto, long userId) {
        Card card = buildNewCard(dto, userId);
        return cardMapper.toDto(cardRepository.save(card));
    }

    @Transactional
    public CardResponseDto update(CardUpdateDto dto, long id) {
        Card existing = getCardById(id);
        cardMapper.updateEntityFromDto(dto, existing);
        return cardMapper.toDto(cardRepository.save(existing));
    }

    @Transactional
    public CardResponseDto blockCard(Long cardId, Long userId) {
        Card card = getCardByOwner(cardId, userId);
        ensureNotBlocked(card);
        card.setCardStatus(CardStatus.BLOCKED);
        return cardMapper.toDto(cardRepository.save(card));
    }

    @Transactional
    public CardResponseDto activateCard(Long cardId) {
        Card card = getCardById(cardId);
        ensureActivatable(card);
        card.setCardStatus(CardStatus.ACTIVE);
        return cardMapper.toDto(cardRepository.save(card));
    }

    @Transactional
    public void deleteCard(Long cardId) {
        cardRepository.delete(getCardById(cardId));
    }

    @Transactional
    public void transferBetweenCards(TransferRequestDto transfer, Long userId) {
        Card fromCard = getCardByOwner(transfer.getFromCardId(), userId);
        Card toCard = getCardByOwner(transfer.getToCardId(), userId);
        validateTransfer(fromCard, toCard, transfer.getAmount());
        fromCard.setBalance(fromCard.getBalance().subtract(transfer.getAmount()));
        toCard.setBalance(toCard.getBalance().add(transfer.getAmount()));
        cardRepository.save(fromCard);
        cardRepository.save(toCard);
    }

    private Card getCardById(long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
    }

    private Card getCardByOwner(Long cardId, Long userId) {
        return cardRepository.findByIdAndOwnerId(cardId, userId)
                .orElseThrow(() -> new CardNotFoundException(cardId));
    }

    private Card buildNewCard(CardRequestDto dto, long userId) {
        Card card = cardMapper.toEntity(dto);
        card.setOwner(userService.findUserById(userId));
        String cardNumber = cardUtils.generateCardNumber();
        card.setCardNumber(cardUtils.encryptCardNumber(cardNumber));
        return card;
    }

    private void ensureNotBlocked(Card card) {
        if (card.getCardStatus() == CardStatus.BLOCKED) {
            throw new InvalidCardOperationException("Карта уже заблокирована");
        }
    }

    private void ensureActivatable(Card card) {
        if (card.getCardStatus() == CardStatus.ACTIVE) {
            throw new InvalidCardOperationException("Карта уже активна");
        }
        if (isExpired(card)) {
            throw new InvalidCardOperationException("Нельзя активировать карту с истекшим сроком действия");
        }
    }

    private boolean isExpired(Card card) {
        return card.getExpirationDate().isBefore(LocalDate.now());
    }

    private void validateTransfer(Card from, Card to, java.math.BigDecimal amount) {
        if (from.getCardStatus() != CardStatus.ACTIVE) {
            throw new InvalidCardOperationException("Карта отправителя неактивна");
        }
        if (to.getCardStatus() != CardStatus.ACTIVE) {
            throw new InvalidCardOperationException("Карта получателя неактивна");
        }
        if (isExpired(from)) {
            throw new InvalidCardOperationException("Срок действия карты отправителя истек");
        }
        if (isExpired(to)) {
            throw new InvalidCardOperationException("Срок действия карты получателя истек");
        }
        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Недостаточно средств на карте отправителя");
        }
    }
}
