package com.Dou888311.antifraud.DTO;

import lombok.Data;

@Data
public class IpDeleteResponse {
    private String status;

    public IpDeleteResponse(String address) {
        status = "IP " + address + " successfully removed!";
    }
}
