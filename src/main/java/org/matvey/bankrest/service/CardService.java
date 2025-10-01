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

/**
 * Сервис для управления банковскими картами.
 * Предоставляет функциональность для создания, поиска, обновления, блокировки карт и переводов между ними.
 */
@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final UserService userService;
    private final CardUtils cardUtils;

    /**
     * Получает список всех карт в системе.
     * Доступно только администраторам.
     *
     * @return список всех карт
     */
    @Transactional(readOnly = true)
    public List<CardResponseDto> findAllCards() {
        return cardMapper.toDto(cardRepository.findAll());
    }

    /**
     * Получает карты пользователя с поддержкой пагинации.
     *
     * @param userId ID пользователя
     * @param pageable параметры пагинации
     * @return страница карт пользователя
     */
    @Transactional(readOnly = true)
    public Page<CardResponseDto> findUserCards(Long userId, Pageable pageable) {
        return cardRepository.findByOwnerId(userId, pageable).map(cardMapper::toDto);
    }

    /**
     * Получает карты пользователя по статусу с поддержкой пагинации.
     *
     * @param userId ID пользователя
     * @param status статус карты для фильтрации
     * @param pageable параметры пагинации
     * @return страница карт пользователя с указанным статусом
     */
    @Transactional(readOnly = true)
    public Page<CardResponseDto> findUserCardsByStatus(Long userId, CardStatus status, Pageable pageable) {
        return cardRepository.findByOwnerIdAndCardStatus(userId, status, pageable).map(cardMapper::toDto);
    }

    /**
     * Находит карту по ID и возвращает DTO.
     *
     * @param id ID карты
     * @return DTO карты
     * @throws CardNotFoundException если карта не найдена
     */
    @Transactional(readOnly = true)
    public CardResponseDto findCardDtoById(long id) {
        return cardMapper.toDto(getCardById(id));
    }

    /**
     * Находит карту пользователя по ID карты и ID пользователя.
     *
     * @param userId ID пользователя-владельца карты
     * @param cardId ID карты
     * @return DTO карты
     * @throws CardNotFoundException если карта не найдена или не принадлежит пользователю
     */
    @Transactional(readOnly = true)
    public CardResponseDto findUserCardById(Long userId, Long cardId) {
        Card card = cardRepository.findByIdAndOwnerId(cardId, userId)
                .orElseThrow(() -> new CardNotFoundException(cardId));
        return cardMapper.toDto(card);
    }

    /**
     * Создает новую банковскую карту для пользователя.
     *
     * @param dto данные для создания карты
     * @param userId ID пользователя-владельца
     * @return DTO созданной карты
     */
    @Transactional
    public CardResponseDto create(CardRequestDto dto, long userId) {
        Card card = buildNewCard(dto, userId);
        return cardMapper.toDto(cardRepository.save(card));
    }

    /**
     * Обновляет данные существующей карты.
     *
     * @param dto данные для обновления карты
     * @param id ID карты для обновления
     * @return DTO обновленной карты
     * @throws CardNotFoundException если карта не найдена
     */
    @Transactional
    public CardResponseDto update(CardUpdateDto dto, long id) {
        Card existing = getCardById(id);
        cardMapper.updateEntityFromDto(dto, existing);
        return cardMapper.toDto(cardRepository.save(existing));
    }

    /**
     * Блокирует карту пользователя.
     *
     * @param cardId ID карты для блокировки
     * @param userId ID пользователя-владельца карты
     * @return DTO заблокированной карты
     * @throws CardNotFoundException если карта не найдена или не принадлежит пользователю
     * @throws InvalidCardOperationException если карта уже заблокирована
     */
    @Transactional
    public CardResponseDto blockCard(Long cardId, Long userId) {
        Card card = getCardByOwner(cardId, userId);
        ensureNotBlocked(card);
        card.setCardStatus(CardStatus.BLOCKED);
        return cardMapper.toDto(cardRepository.save(card));
    }

    /**
     * Активирует карту. Доступно только администраторам.
     *
     * @param cardId ID карты для активации
     * @return DTO активированной карты
     * @throws CardNotFoundException если карта не найдена
     * @throws InvalidCardOperationException если карта уже активна или истек срок действия
     */
    @Transactional
    public CardResponseDto activateCard(Long cardId) {
        Card card = getCardById(cardId);
        ensureActivatable(card);
        card.setCardStatus(CardStatus.ACTIVE);
        return cardMapper.toDto(cardRepository.save(card));
    }

    /**
     * Удаляет карту из системы. Доступно только администраторам.
     *
     * @param cardId ID карты для удаления
     * @throws CardNotFoundException если карта не найдена
     */
    @Transactional
    public void deleteCard(Long cardId) {
        cardRepository.delete(getCardById(cardId));
    }

    /**
     * Выполняет перевод средств между картами пользователя.
     *
     * @param transfer данные перевода (карты отправителя и получателя, сумма)
     * @param userId ID пользователя-владельца карт
     * @throws CardNotFoundException если одна из карт не найдена или не принадлежит пользователю
     * @throws InvalidCardOperationException если карты неактивны или истек срок действия
     * @throws InsufficientFundsException если недостаточно средств на карте отправителя
     */
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

    /**
     * Находит карту по ID или выбрасывает исключение.
     *
     * @param id ID карты
     * @return сущность карты
     * @throws CardNotFoundException если карта не найдена
     */
    private Card getCardById(long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new CardNotFoundException(id));
    }

    /**
     * Находит карту по ID карты и ID владельца или выбрасывает исключение.
     *
     * @param cardId ID карты
     * @param userId ID владельца карты
     * @return сущность карты
     * @throws CardNotFoundException если карта не найдена или не принадлежит пользователю
     */
    private Card getCardByOwner(Long cardId, Long userId) {
        return cardRepository.findByIdAndOwnerId(cardId, userId)
                .orElseThrow(() -> new CardNotFoundException(cardId));
    }

    /**
     * Создает новую сущность карты на основе DTO и данных пользователя.
     * Генерирует и шифрует номер карты, устанавливает владельца.
     *
     * @param dto данные для создания карты
     * @param userId ID пользователя-владельца
     * @return подготовленная сущность карты
     */
    private Card buildNewCard(CardRequestDto dto, long userId) {
        Card card = cardMapper.toEntity(dto);
        card.setOwner(userService.findUserById(userId));
        String cardNumber = cardUtils.generateCardNumber();
        card.setCardNumber(cardUtils.encryptCardNumber(cardNumber));
        return card;
    }

    /**
     * Проверяет, что карта не заблокирована.
     *
     * @param card карта для проверки
     * @throws InvalidCardOperationException если карта уже заблокирована
     */
    private void ensureNotBlocked(Card card) {
        if (card.getCardStatus() == CardStatus.BLOCKED) {
            throw new InvalidCardOperationException("Карта уже заблокирована");
        }
    }

    /**
     * Проверяет, что карту можно активировать.
     * Карта не должна быть уже активной и не должна быть просроченной.
     *
     * @param card карта для проверки
     * @throws InvalidCardOperationException если карта уже активна или просрочена
     */
    private void ensureActivatable(Card card) {
        if (card.getCardStatus() == CardStatus.ACTIVE) {
            throw new InvalidCardOperationException("Карта уже активна");
        }
        if (isExpired(card)) {
            throw new InvalidCardOperationException("Нельзя активировать карту с истекшим сроком действия");
        }
    }

    /**
     * Проверяет, истек ли срок действия карты.
     *
     * @param card карта для проверки
     * @return true, если срок действия карты истек
     */
    private boolean isExpired(Card card) {
        return card.getExpirationDate().isBefore(LocalDate.now());
    }

    /**
     * Валидирует возможность выполнения перевода между картами.
     * Проверяет статусы карт, сроки действия и достаточность средств.
     *
     * @param from карта отправителя
     * @param to карта получателя
     * @param amount сумма перевода
     * @throws InvalidCardOperationException если карты неактивны или просрочены
     * @throws InsufficientFundsException если недостаточно средств на карте отправителя
     */
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
