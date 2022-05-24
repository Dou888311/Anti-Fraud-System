package com.Dou888311.antifraud.repository;

import com.Dou888311.antifraud.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findAllTransactionByNumberAndDateBetween (String number, LocalDateTime earlyHour, LocalDateTime currentTransaction);
    Boolean existsByNumber(String number);
    List<Transaction> findAllTransactionByNumber(String number);
    Transaction findTransactionById(long id);
}
