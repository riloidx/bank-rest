package org.matvey.bankrest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.matvey.bankrest.dto.request.CardRequestDto;
import org.matvey.bankrest.dto.request.CardUpdateDto;
import org.matvey.bankrest.dto.request.TransferRequestDto;
import org.matvey.bankrest.dto.response.CardResponseDto;
import org.matvey.bankrest.dto.response.ErrorResponse;
import org.matvey.bankrest.dto.response.ValidationErrorResponse;
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
@Tag(name = "Управление картами", description = "API для управления банковскими картами")
@SecurityRequirement(name = "bearerAuth")
public class CardController {
    private final CardService cardService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить все карты", 
               description = "Возвращает список всех карт в системе. Доступно только администраторам.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список карт успешно получен",
                    content = @Content(mediaType = "application/json", 
                                     array = @ArraySchema(schema = @Schema(implementation = CardResponseDto.class)))),
        @ApiResponse(responseCode = "401", description = "Не авторизован",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "403", description = "Недостаточно прав доступа",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<CardResponseDto>> getAllCards() {
        List<CardResponseDto> cards = cardService.findAllCards();
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/my")
    @Operation(summary = "Получить мои карты", 
               description = "Возвращает карты текущего пользователя с поддержкой пагинации")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Карты пользователя успешно получены",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = Page.class))),
        @ApiResponse(responseCode = "401", description = "Не авторизован",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Page<CardResponseDto>> getMyCards(
            Authentication authentication,
            @Parameter(description = "Параметры пагинации") @PageableDefault(size = 10) Pageable pageable) {
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
    @Operation(summary = "Создать новую карту", 
               description = "Создает новую банковскую карту для текущего пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Карта успешно создана",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = CardResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Не авторизован",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ErrorResponse.class)))
    })
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
    @Operation(summary = "Перевод между картами", 
               description = "Осуществляет перевод средств между картами текущего пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Перевод успешно выполнен"),
        @ApiResponse(responseCode = "400", description = "Ошибка валидации или недостаточно средств",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Не авторизован",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Карта не найдена",
                    content = @Content(mediaType = "application/json", 
                                     schema = @Schema(implementation = ErrorResponse.class)))
    })
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
