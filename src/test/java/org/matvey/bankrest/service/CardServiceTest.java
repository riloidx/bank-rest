package org.matvey.bankrest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.matvey.bankrest.dto.request.CardRequestDto;
import org.matvey.bankrest.dto.request.TransferRequestDto;
import org.matvey.bankrest.entity.Card;
import org.matvey.bankrest.entity.CardStatus;
import org.matvey.bankrest.entity.User;
import org.matvey.bankrest.exception.CardNotFoundException;
import org.matvey.bankrest.exception.InsufficientFundsException;
import org.matvey.bankrest.exception.InvalidCardOperationException;
import org.matvey.bankrest.mapper.CardMapper;
import org.matvey.bankrest.repository.CardRepository;
import org.matvey.bankrest.util.CardUtils;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private UserService userService;

    @Mock
    private CardUtils cardUtils;

    @InjectMocks
    private CardService cardService;

    private User testUser;
    private Card testCard;
    private CardRequestDto cardRequestDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        testCard = new Card();
        testCard.setId(1L);
        testCard.setCardNumber("encrypted_card_number");
        testCard.setExpirationDate(LocalDate.now().plusYears(3));
        testCard.setCardStatus(CardStatus.ACTIVE);
        testCard.setBalance(BigDecimal.valueOf(1000.00));
        testCard.setOwner(testUser);

        cardRequestDto = new CardRequestDto();
        cardRequestDto.setExpirationDate(LocalDate.now().plusYears(3));
        cardRequestDto.setCardStatus(CardStatus.ACTIVE);
        cardRequestDto.setBalance(BigDecimal.ZERO);
    }

    @Test
    void createCard_ShouldCreateCardSuccessfully() {
        when(userService.findUserById(1L)).thenReturn(testUser);
        when(cardMapper.toEntity(cardRequestDto)).thenReturn(testCard);
        when(cardUtils.generateCardNumber()).thenReturn("1234567890123456");
        when(cardUtils.encryptCardNumber("1234567890123456")).thenReturn("encrypted_card_number");
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        cardService.create(cardRequestDto, 1L);
        verify(cardUtils).generateCardNumber();
        verify(cardUtils).encryptCardNumber("1234567890123456");
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void findCardById_WhenCardExists_ShouldReturnCard() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));

        Card result = cardService.findCardById(1L);
        assertNotNull(result);
        assertEquals(testCard.getId(), result.getId());
    }

    @Test
    void findCardById_WhenCardNotExists_ShouldThrowException() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CardNotFoundException.class, () -> cardService.findCardById(1L));
    }

    @Test
    void blockCard_WhenCardExists_ShouldBlockCard() {
        when(cardRepository.findByIdAndOwnerId(1L, 1L)).thenReturn(Optional.of(testCard));
        when(cardRepository.save(any(Card.class))).thenReturn(testCard);

        cardService.blockCard(1L, 1L);
        assertEquals(CardStatus.BLOCKED, testCard.getCardStatus());
        verify(cardRepository).save(testCard);
    }

    @Test
    void blockCard_WhenCardAlreadyBlocked_ShouldThrowException() {
        testCard.setCardStatus(CardStatus.BLOCKED);
        when(cardRepository.findByIdAndOwnerId(1L, 1L)).thenReturn(Optional.of(testCard));
        assertThrows(InvalidCardOperationException.class, () -> cardService.blockCard(1L, 1L));
    }

    @Test
    void transferBetweenCards_WhenSufficientFunds_ShouldTransferSuccessfully() {
        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setBalance(BigDecimal.valueOf(1000.00));
        fromCard.setCardStatus(CardStatus.ACTIVE);
        fromCard.setExpirationDate(LocalDate.now().plusYears(1));
        fromCard.setOwner(testUser);

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setBalance(BigDecimal.valueOf(500.00));
        toCard.setCardStatus(CardStatus.ACTIVE);
        toCard.setExpirationDate(LocalDate.now().plusYears(1));
        toCard.setOwner(testUser);

        TransferRequestDto transferRequest = new TransferRequestDto();
        transferRequest.setFromCardId(1L);
        transferRequest.setToCardId(2L);
        transferRequest.setAmount(BigDecimal.valueOf(200.00));

        when(cardRepository.findByIdAndOwnerId(1L, 1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndOwnerId(2L, 1L)).thenReturn(Optional.of(toCard));
        when(cardRepository.save(any(Card.class))).thenReturn(fromCard, toCard);

        cardService.transferBetweenCards(transferRequest, 1L);
        assertEquals(BigDecimal.valueOf(800.00), fromCard.getBalance());
        assertEquals(BigDecimal.valueOf(700.00), toCard.getBalance());
        verify(cardRepository, times(2)).save(any(Card.class));
    }

    @Test
    void transferBetweenCards_WhenInsufficientFunds_ShouldThrowException() {
        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setBalance(BigDecimal.valueOf(100.00));
        fromCard.setCardStatus(CardStatus.ACTIVE);
        fromCard.setExpirationDate(LocalDate.now().plusYears(1));
        fromCard.setOwner(testUser);

        Card toCard = new Card();
        toCard.setId(2L);
        toCard.setBalance(BigDecimal.valueOf(500.00));
        toCard.setCardStatus(CardStatus.ACTIVE);
        toCard.setExpirationDate(LocalDate.now().plusYears(1));
        toCard.setOwner(testUser);

        TransferRequestDto transferRequest = new TransferRequestDto();
        transferRequest.setFromCardId(1L);
        transferRequest.setToCardId(2L);
        transferRequest.setAmount(BigDecimal.valueOf(200.00));

        when(cardRepository.findByIdAndOwnerId(1L, 1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdAndOwnerId(2L, 1L)).thenReturn(Optional.of(toCard));
        assertThrows(InsufficientFundsException.class, () -> 
            cardService.transferBetweenCards(transferRequest, 1L));
    }

    @Test
    void transferBetweenCards_WhenCardInactive_ShouldThrowException() {
        Card fromCard = new Card();
        fromCard.setId(1L);
        fromCard.setBalance(BigDecimal.valueOf(1000.00));
        fromCard.setCardStatus(CardStatus.BLOCKED);
        fromCard.setExpirationDate(LocalDate.now().plusYears(1));
        fromCard.setOwner(testUser);

        TransferRequestDto transferRequest = new TransferRequestDto();
        transferRequest.setFromCardId(1L);
        transferRequest.setToCardId(2L);
        transferRequest.setAmount(BigDecimal.valueOf(200.00));

        when(cardRepository.findByIdAndOwnerId(1L, 1L)).thenReturn(Optional.of(fromCard));
        assertThrows(InvalidCardOperationException.class, () -> 
            cardService.transferBetweenCards(transferRequest, 1L));
    }
}
