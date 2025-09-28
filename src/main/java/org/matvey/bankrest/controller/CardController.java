package org.matvey.bankrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.matvey.bankrest.dto.request.CardRequestDto;
import org.matvey.bankrest.dto.request.CardUpdateDto;
import org.matvey.bankrest.dto.request.TransferRequestDto;
import org.matvey.bankrest.dto.response.CardResponseDto;
import org.matvey.bankrest.entity.CardStatus;
import org.matvey.bankrest.service.CardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
@Tag(name = "Card Management", description = "API для управления картами")
public class CardController {
    private final CardService cardService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить все карты", description = "Доступно только администраторам")
    public ResponseEntity<List<CardResponseDto>> getAllCards() {
        List<CardResponseDto> cards = cardService.findAllCards();
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/my")
    @Operation(summary = "Получить мои карты", description = "Получить карты текущего пользователя с пагинацией")
    public ResponseEntity<Page<CardResponseDto>> getMyCards(
            Authentication authentication,
            @PageableDefault(size = 10) Pageable pageable) {
        Long userId = getCurrentUserId(authentication);
        Page<CardResponseDto> cards = cardService.findUserCards(userId, pageable);

        return ResponseEntity.ok(cards);
    }

    @GetMapping("/my/status/{status}")
    @Operation(summary = "Получить мои карты по статусу", description = "Получить карты текущего пользователя по статусу")
    public ResponseEntity<Page<CardResponseDto>> getMyCardsByStatus(
            Authentication authentication,
            @Parameter(description = "Статус карты") @PathVariable CardStatus status,
            @PageableDefault(size = 10) Pageable pageable) {
        Long userId = getCurrentUserId(authentication);
        Page<CardResponseDto> cards = cardService.findUserCardsByStatus(userId, status, pageable);

        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить карту по ID", description = "Получить карту по ID (пользователи видят только свои карты)")
    public ResponseEntity<CardResponseDto> getCardById(
            @Parameter(description = "ID карты") @PathVariable Long id,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        CardResponseDto card = cardService.findUserCardById(userId, id);

        return ResponseEntity.ok(card);
    }

    @PostMapping
    @Operation(summary = "Создать новую карту", description = "Создать новую карту для текущего пользователя")
    public ResponseEntity<CardResponseDto> createCard(
            @Valid @RequestBody CardRequestDto cardRequestDto,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        CardResponseDto card = cardService.create(cardRequestDto, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(card);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить карту", description = "Обновить карту (доступно только администраторам)")
    public ResponseEntity<CardResponseDto> updateCard(
            @Parameter(description = "ID карты") @PathVariable Long id,
            @Valid @RequestBody CardUpdateDto cardUpdateDto) {
        CardResponseDto card = cardService.update(cardUpdateDto, id);

        return ResponseEntity.ok(card);
    }

    @PostMapping("/{id}/block")
    @Operation(summary = "Заблокировать карту", description = "Заблокировать карту (пользователи могут блокировать только свои карты)")
    public ResponseEntity<CardResponseDto> blockCard(
            @Parameter(description = "ID карты") @PathVariable Long id,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        CardResponseDto card = cardService.blockCard(id, userId);

        return ResponseEntity.ok(card);
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Активировать карту", description = "Активировать карту (доступно только администраторам)")
    public ResponseEntity<CardResponseDto> activateCard(
            @Parameter(description = "ID карты") @PathVariable Long id) {
        CardResponseDto card = cardService.activateCard(id);

        return ResponseEntity.ok(card);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить карту", description = "Удалить карту (доступно только администраторам)")
    public ResponseEntity<Void> deleteCard(
            @Parameter(description = "ID карты") @PathVariable Long id) {
        cardService.deleteCard(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/transfer")
    @Operation(summary = "Перевод между картами", description = "Перевод средств между картами текущего пользователя")
    public ResponseEntity<Void> transferBetweenCards(
            @Valid @RequestBody TransferRequestDto transferRequest,
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        cardService.transferBetweenCards(transferRequest, userId);

        return ResponseEntity.ok().build();
    }

    private Long getCurrentUserId(Authentication authentication) {
        return ((org.matvey.bankrest.security.CustomUserDetails) authentication.getPrincipal()).getId();
    }
}
