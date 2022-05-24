package com.Dou888311.antifraud.repository;

import com.Dou888311.antifraud.Entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, String> {
    Boolean existsByNumber(String number);
    Card findCardByNumber(String number);
}
