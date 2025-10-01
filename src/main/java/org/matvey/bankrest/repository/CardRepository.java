package org.matvey.bankrest.repository;

import org.matvey.bankrest.entity.Card;
import org.matvey.bankrest.entity.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностями Card.
 * Предоставляет методы для поиска карт по различным критериям.
 */
public interface CardRepository extends JpaRepository<Card, Long> {
    
    /**
     * Находит карты по ID владельца с поддержкой пагинации.
     *
     * @param ownerId ID владельца карт
     * @param pageable параметры пагинации
     * @return страница карт владельца
     */
    Page<Card> findByOwnerId(Long ownerId, Pageable pageable);
    
    /**
     * Находит карты по ID владельца и статусу карты.
     *
     * @param ownerId ID владельца карт
     * @param status статус карты
     * @param pageable параметры пагинации
     * @return страница карт с указанным статусом
     */
    Page<Card> findByOwnerIdAndCardStatus(Long ownerId, CardStatus status, Pageable pageable);
    
    /**
     * Находит карту по ID карты и ID владельца.
     *
     * @param id ID карты
     * @param ownerId ID владельца
     * @return Optional с картой, если найдена
     */
    Optional<Card> findByIdAndOwnerId(Long id, Long ownerId);
    
    /**
     * Находит карты пользователя по статусу с использованием JPQL запроса.
     *
     * @param ownerId ID владельца карт
     * @param status статус карты
     * @param pageable параметры пагинации
     * @return страница карт с указанным статусом
     */
    @Query("SELECT c FROM Card c WHERE c.owner.id = :ownerId AND c.cardStatus = :status")
    Page<Card> findUserCardsByStatus(@Param("ownerId") Long ownerId, @Param("status") CardStatus status, Pageable pageable);
}
