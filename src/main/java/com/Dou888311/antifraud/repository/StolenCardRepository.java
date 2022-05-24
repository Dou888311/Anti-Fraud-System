package com.Dou888311.antifraud.repository;

import com.Dou888311.antifraud.Entity.StolenCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StolenCardRepository extends JpaRepository<StolenCard, Long> {

    Boolean existsByNumber(String number);

    StolenCard findStolenCardByNumber(String number);
}
