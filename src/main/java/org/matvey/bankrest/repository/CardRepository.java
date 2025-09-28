package org.matvey.bankrest.repository;

import org.matvey.bankrest.entity.Card;
import org.matvey.bankrest.entity.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    
    Page<Card> findByOwnerId(Long ownerId, Pageable pageable);
    
    Page<Card> findByOwnerIdAndCardStatus(Long ownerId, CardStatus status, Pageable pageable);
    
    Optional<Card> findByIdAndOwnerId(Long id, Long ownerId);
    
    @Query("SELECT c FROM Card c WHERE c.owner.id = :ownerId AND c.cardStatus = :status")
    Page<Card> findUserCardsByStatus(@Param("ownerId") Long ownerId, @Param("status") CardStatus status, Pageable pageable);
}
