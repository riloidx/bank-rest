package org.matvey.bankrest.repository;

import org.matvey.bankrest.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
}
