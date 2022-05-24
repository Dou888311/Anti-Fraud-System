package com.Dou888311.antifraud.Service;

import com.Dou888311.antifraud.Config.WebSecurityConfig;
import com.Dou888311.antifraud.DTO.StolenCardDeleteResponse;
import com.Dou888311.antifraud.Entity.StolenCard;
import com.Dou888311.antifraud.repository.StolenCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StolenCardService {

    private WebSecurityConfig webSecurityConfig;
    private StolenCardRepository stolenCardRepository;

    @Autowired
    public StolenCardService(WebSecurityConfig webSecurityConfig, StolenCardRepository stolenCardRepository) {
        this.webSecurityConfig = webSecurityConfig;
        this.stolenCardRepository = stolenCardRepository;
    }

    public StolenCard stolenCardReg(StolenCard stolenCard) {
        if (stolenCardRepository.existsByNumber(stolenCard.getNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        if (isLuhn(stolenCard.getNumber())) {
            return stolenCardRepository.save(stolenCard);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public StolenCardDeleteResponse stolenCardDelete(String number) {
        if (!isLuhn(number)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        StolenCard stolenCard = Optional
                .ofNullable(stolenCardRepository.findStolenCardByNumber(number))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        stolenCardRepository.delete(stolenCard);
        return new StolenCardDeleteResponse(number);
    }

    public List<StolenCard> getStolenCardList() {
        return stolenCardRepository.findAll();
    }

    public static boolean isLuhn(String receiver) {
        if (receiver.length() != 16) {
            return false;
        }
        ArrayList<Integer> digits = new ArrayList<>();
        String cardnumber = receiver;
        int summ = 0;
        for (int i = 0; i < cardnumber.length(); i++) {
            char ch = cardnumber.charAt(i);
            digits.add(Character.getNumericValue(ch));
        }
        digits.remove(15);
        for (int i = 0; i < digits.size(); i++) {
            if (i % 2 == 0) {
                digits.set(i, digits.get(i) * 2);
            }
        }
        for (int i = 0; i < digits.size(); i++) {
            if (digits.get(i) > 9) {
                digits.set(i, digits.get(i) - 9);
            }
        }
        for (int i = 0; i < digits.size(); i++) {
            summ += digits.get(i);
        }
        if (summ % 10 == 0) {
            digits.add(0);
        } else {
            int checksumm = 10 - (summ % 10);
            digits.add(checksumm);
        }
        StringBuilder sb = new StringBuilder(cardnumber);
        sb.deleteCharAt(15);
        sb.append(digits.get(15));
        String luhnNumber = sb.toString();
        if (luhnNumber.equals(receiver)) {
            return true;
        }
        return false;
    }
}
