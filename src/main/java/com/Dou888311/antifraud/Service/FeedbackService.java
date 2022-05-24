package com.Dou888311.antifraud.Service;

import com.Dou888311.antifraud.DTO.Feedback;
import com.Dou888311.antifraud.Entity.Card;
import com.Dou888311.antifraud.Entity.Transaction;
import com.Dou888311.antifraud.repository.CardRepository;
import com.Dou888311.antifraud.repository.TransactionRepository;
import com.Dou888311.antifraud.transaction.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
public class FeedbackService {
    private CardRepository cardRepository;
    private TransactionRepository transactionRepository;

    @Autowired
    public FeedbackService(CardRepository cardRepository, TransactionRepository transactionRepository) {
        this.cardRepository = cardRepository;
        this.transactionRepository = transactionRepository;
    }

    public Transaction setFeedback(Feedback feedback) {
        if (!transactionRepository.existsById((feedback.getTransactionId()))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Transaction transaction = transactionRepository.findTransactionById(feedback.getTransactionId());

        Result result;
        try  {
            result = Result.valueOf(feedback.getFeedback());
        } catch(IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (!Objects.equals(transaction.getFeedback(), "")) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        if (result == transaction.getResult()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return setNewValue(transaction, feedback);
    }

    public Transaction setNewValue(Transaction transaction, Feedback feedback) {
        if (transaction.getResult() == Result.ALLOWED) {
            ifAllow(transaction, feedback);
            transaction.setFeedback(feedback.getFeedback());
            transactionRepository.save(transaction);
            return transaction;
        }
        if (transaction.getResult() == Result.MANUAL_PROCESSING) {
            ifManual(transaction, feedback);
            transaction.setFeedback(feedback.getFeedback());
            transactionRepository.save(transaction);
            return transaction;
        }
        ifProhibited(transaction, feedback);
        transaction.setFeedback(feedback.getFeedback());
        transactionRepository.save(transaction);
        return transaction;
    }

    public void ifAllow(Transaction transaction, Feedback feedback) {
        Card card = cardRepository.findCardByNumber(transaction.getNumber());
        if (feedback.getFeedback().equals("MANUAL_PROCESSING")) {
            card.setMAX_ALLOW((int) Math.ceil(0.8 * card.getMAX_ALLOW() - 0.2 * transaction.getAmount()));
            cardRepository.save(card);
        }
        if (feedback.getFeedback().equals("PROHIBITED")) {
            card.setMAX_ALLOW((int) Math.ceil(0.8 * card.getMAX_ALLOW() - 0.2 * transaction.getAmount()));
            card.setMIN_PROHIBITED((int) Math.ceil(0.8 * card.getMIN_PROHIBITED() - 0.2 * transaction.getAmount()));
            cardRepository.save(card);
        }
    }

    public void ifManual(Transaction transaction, Feedback feedback) {
        Card card = cardRepository.findCardByNumber(transaction.getNumber());
        if (feedback.getFeedback().equals("ALLOWED")) {
            card.setMAX_ALLOW((int) Math.ceil(0.8 * card.getMAX_ALLOW() + 0.2 * transaction.getAmount()));
            cardRepository.save(card);
        }
        if (feedback.getFeedback().equals("PROHIBITED")) {
            card.setMIN_PROHIBITED((int) Math.ceil(0.8 * card.getMIN_PROHIBITED() - 0.2 * transaction.getAmount()));
            cardRepository.save(card);
        }
    }

    public void ifProhibited(Transaction transaction, Feedback feedback) {
        Card card = cardRepository.findCardByNumber(transaction.getNumber());
        if (feedback.getFeedback().equals("ALLOWED")) {
            card.setMAX_ALLOW((int) Math.ceil(0.8 * card.getMAX_ALLOW() + 0.2 * transaction.getAmount()));
            card.setMIN_PROHIBITED((int) Math.ceil(0.8 * card.getMIN_PROHIBITED() + 0.2 * transaction.getAmount()));
            cardRepository.save(card);
        }
        if (feedback.getFeedback().equals("MANUAL_PROCESSING")) {
            card.setMIN_PROHIBITED((int) Math.ceil(0.8 * card.getMIN_PROHIBITED() + 0.2 * transaction.getAmount()));
            cardRepository.save(card);
        }
    }
}
