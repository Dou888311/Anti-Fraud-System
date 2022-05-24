package com.Dou888311.antifraud.DTO;

import lombok.Data;

@Data
public class UserOperationResponse {
    private String status;

    public UserOperationResponse(String username, String operation) {
        status = "User " + username + " " + operation + "!";
    }
}
