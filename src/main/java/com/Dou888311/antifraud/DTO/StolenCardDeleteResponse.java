package com.Dou888311.antifraud.DTO;

import lombok.Data;

@Data
public class StolenCardDeleteResponse {

    private String status;
    public StolenCardDeleteResponse(String number) {
        status = "Card " + number + " successfully removed!";
    }
}
