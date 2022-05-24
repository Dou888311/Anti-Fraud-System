package com.Dou888311.antifraud.Controller;

import com.Dou888311.antifraud.DTO.StolenCardDeleteResponse;
import com.Dou888311.antifraud.Entity.StolenCard;
import com.Dou888311.antifraud.Service.StolenCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class StolenCardController {
    private StolenCardService stolenCardService;

    @Autowired
    public StolenCardController(StolenCardService stolenCardService) {
        this.stolenCardService = stolenCardService;
    }

    @PostMapping("/api/antifraud/stolencard")
    public StolenCard stoleCardReg(@RequestBody StolenCard stolenCard) {
        return stolenCardService.stolenCardReg(stolenCard);
    }

    @DeleteMapping("/api/antifraud/stolencard/{number}")
    public StolenCardDeleteResponse stolenCardDelete(@PathVariable String number) {
        return stolenCardService.stolenCardDelete(number);
    }

    @GetMapping("/api/antifraud/stolencard")
    public List<StolenCard> getStolenCardList() {
        return stolenCardService.getStolenCardList();
    }
}
