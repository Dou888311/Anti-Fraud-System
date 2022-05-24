package com.Dou888311.antifraud.DTO;

import lombok.Data;

@Data
public class UserDeleteResponse {
    public UserDeleteResponse(String username) {
        this.username = username;
    }

    private String username;
    private String status = "Deleted successfully!";
}
