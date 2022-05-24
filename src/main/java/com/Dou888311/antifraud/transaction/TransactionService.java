package com.Dou888311.antifraud.transaction;

import com.Dou888311.antifraud.DTO.TransactionResponse;
import com.Dou888311.antifraud.Entity.Card;
import com.Dou888311.antifraud.Entity.Transaction;
import com.Dou888311.antifraud.Service.StolenCardService;
import com.Dou888311.antifraud.repository.CardRepository;
import com.Dou888311.antifraud.repository.StolenCardRepository;
import com.Dou888311.antifraud.repository.SuspiciousIpRepository;
import com.Dou888311.antifraud.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private SuspiciousIpRepository suspiciousIpRepository;
    private StolenCardRepository stolenCardRepository;
    private TransactionRepository transactionRepository;
    private ArrayList<String> info;
    private TransactionResponse response;
    private CardRepository cardRepository;

    @Autowired
    public TransactionService(SuspiciousIpRepository suspiciousIpRepository, StolenCardRepository stolenCardRepository,
                              TransactionRepository transactionRepository, CardRepository cardRepository) {
        this.suspiciousIpRepository = suspiciousIpRepository;
        this.stolenCardRepository = stolenCardRepository;
        this.transactionRepository = transactionRepository;
        this.cardRepository = cardRepository;
    }

    public List<Transaction> getHistory() {
        return transactionRepository.findAll();
    }

    public List<Transaction> getSpecifiedHistory(String number) {
        if (!StolenCardService.isLuhn(number)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (!transactionRepository.existsByNumber(number)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return transactionRepository.findAllTransactionByNumber(number);
    }

    public ResponseEntity<TransactionResponse> transaction(Transaction transaction) {

        response = new TransactionResponse();
        info = new ArrayList<>();
        response.setResult("");

        if (!ipValidCheck(transaction) || !isCardLuhn(transaction)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        cardSave(transaction.getNumber());

        transactionProhibitedCheck(transaction);
        if(response.getResult().equals("PROHIBITED")) {
            transaction.setResult(Result.PROHIBITED);
            return responsePrint(transaction);}

        transactionManualCheck(transaction);
        if(response.getResult().equals("MANUAL_PROCESSING")) {
            transaction.setResult(Result.MANUAL_PROCESSING);
            return responsePrint(transaction);}

        amountCheck(transaction);
        transaction.setResult(Result.ALLOWED);
        return responsePrint(transaction);
    }

    public ResponseEntity<TransactionResponse> responsePrint(Transaction transaction) {
        info.sort(String::compareToIgnoreCase);
        String res = info.get(0);
        StringBuilder sb = new StringBuilder(res);
        for (int i = 1; i < info.size(); i++) {
            sb.append(", " + info.get(i));
        }
        res = sb.toString();
        response.setInfo(res);
        transactionRepository.save(transaction);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public void cardSave(String number) {
        if (!cardRepository.existsByNumber(number)) {
            cardRepository.save(new Card(number));
        }
    }

    public void transactionManualCheck(Transaction transaction) {
        Card card = cardRepository.findCardByNumber(transaction.getNumber());
        if (isRegionManual(transaction)) {
            info.add("region-correlation");
            response.setResult("MANUAL_PROCESSING");
        }
        if (isIpManual(transaction)) {
            info.add("ip-correlation");
            response.setResult("MANUAL_PROCESSING");
        }
        if (transaction.getAmount() > card.getMAX_ALLOW() && transaction.getAmount() <= card.getMIN_PROHIBITED()) {
            response.setResult("MANUAL_PROCESSING");
            info.add("amount");
        }
    }

    public void transactionProhibitedCheck(Transaction transaction) {
        Card card = cardRepository.findCardByNumber(transaction.getNumber());
        if (isCardStolen(transaction)) {
            info.add("card-number");
            response.setResult("PROHIBITED");
        }
        if (isIpSuspicious(transaction)) {
            info.add("ip");
            response.setResult("PROHIBITED");

        }
        if (transaction.getAmount() > card.getMIN_PROHIBITED()) {
            info.add("amount");
            response.setResult("PROHIBITED");
        }
        if (isRegionCorrelation(transaction)) {
            info.add("region-correlation");
            response.setResult("PROHIBITED");
        }
        if (isIpCorrelation(transaction)) {
            info.add("ip-correlation");
            response.setResult("PROHIBITED");
        }
    }

    public void amountCheck(Transaction transaction) {
        Card card = cardRepository.findCardByNumber(transaction.getNumber());
        if (transaction.getAmount() <= card.getMAX_ALLOW() && transaction.getAmount() > 0) {
            response.setResult("ALLOWED");
            info.add("none");
        }
    }

    public boolean isRegionCorrelation(Transaction transaction) {
        LocalDateTime transactionDate = transaction.getDate();
        LocalDateTime earlyHour = transactionDate.minusHours(1);
        List<Transaction> transactions = transactionRepository.findAllTransactionByNumberAndDateBetween(transaction.getNumber(), earlyHour, transactionDate);
        Region region = transaction.getRegion();
        Set<Region> original = transactions.stream()
                .map(Transaction::getRegion)
                .collect(Collectors.toSet());
        int regionCount = 0;
        for (Region reg : original) {
            if (reg != region) {
                regionCount++;
            }
        }
        return regionCount > 2;
    }

    public boolean isRegionManual(Transaction transaction) {
        LocalDateTime transactionDate = transaction.getDate();
        LocalDateTime earlyHour = transactionDate.minusHours(1);
        List<Transaction> transactions = transactionRepository.findAllTransactionByNumberAndDateBetween(transaction.getNumber(), earlyHour, transactionDate);
        Region region = transaction.getRegion();
        Set<Region> original = transactions.stream()
                .map(Transaction::getRegion)
                .collect(Collectors.toSet());
        int regionCount = 0;
        for (Region reg : original) {
            if (reg != region) {
                regionCount++;
            }
        }
        return regionCount == 2;
    }

    public boolean isIpCorrelation(Transaction transaction) {
        LocalDateTime transactionDate = transaction.getDate();
        LocalDateTime earlyHour = transactionDate.minusHours(1);
        List<Transaction> transactions = transactionRepository.findAllTransactionByNumberAndDateBetween(
                transaction.getNumber(), earlyHour, transactionDate);
        String ip = transaction.getIp();
        int ipCounter = 0;
        Set<String> original = transactions.stream()
                .map(Transaction::getIp)
                .collect(Collectors.toSet());
        for (String or : original) {
            if (!or.equals(ip)) {
                ipCounter++;
            }
        }
        return ipCounter > 2;
    }

    public boolean isIpManual(Transaction transaction) {
        LocalDateTime transactionDate = transaction.getDate();
        LocalDateTime earlyHour = transactionDate.minusHours(1).plusSeconds(1);
        List<Transaction> transactions = transactionRepository.findAllTransactionByNumberAndDateBetween(
                transaction.getNumber(), earlyHour, transactionDate);
        String ip = transaction.getIp();
        int ipCounter = 0;
        Set<String> original = transactions.stream()
                .map(Transaction::getIp)
                .collect(Collectors.toSet());
        for (String or : original) {
            if (!or.equals(ip)) {
                ipCounter++;
            }
        }
        return ipCounter == 2;
    }

    public boolean isCardStolen(Transaction transaction) {
        return stolenCardRepository.existsByNumber(transaction.getNumber());
    }
    public boolean isIpSuspicious(Transaction transaction) {
        return suspiciousIpRepository.existsByIp(transaction.getIp());
    }
    public boolean isCardLuhn(Transaction transaction) {
        return StolenCardService.isLuhn(transaction.getNumber());
    }
    public boolean ipValidCheck(Transaction transaction) {
        String regex = "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]).([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])." +
                "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]).([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])";
        return transaction.getIp().matches(regex);
    }
}
