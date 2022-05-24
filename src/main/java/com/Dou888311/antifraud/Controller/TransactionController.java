package com.Dou888311.antifraud.Controller;

import com.Dou888311.antifraud.DTO.Feedback;
import com.Dou888311.antifraud.DTO.TransactionResponse;
import com.Dou888311.antifraud.Entity.Transaction;
import com.Dou888311.antifraud.Service.FeedbackService;
import com.Dou888311.antifraud.transaction.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class TransactionController {

    private TransactionService transactionService;
    private FeedbackService feedbackService;

    @Autowired
    public TransactionController(TransactionService transactionService, FeedbackService feedbackService) {
        this.transactionService = transactionService;
        this.feedbackService = feedbackService;
    }

    @PostMapping("/api/antifraud/transaction")
    @ResponseBody
    public ResponseEntity<TransactionResponse> transaction(@RequestBody @Valid Transaction transaction) {
        return transactionService.transaction(transaction);
    }

    @GetMapping("/api/antifraud/history")
    public List<Transaction> getHistory() {
        return transactionService.getHistory();
    }

    @GetMapping("/api/antifraud/history/{number}")
    public List<Transaction> getSpecifiedHistory(@PathVariable String number) {
        return transactionService.getSpecifiedHistory(number);
    }

    @PutMapping("/api/antifraud/transaction")
    public Transaction setFeedback(@RequestBody Feedback feedback) {
        return feedbackService.setFeedback(feedback);
    }
}
