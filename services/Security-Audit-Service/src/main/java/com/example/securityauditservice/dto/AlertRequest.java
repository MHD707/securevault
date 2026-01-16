package com.example.securityauditservice.dto;

import lombok.Data;

@Data
public class AlertRequest {
    private String vaultItemId;
    private int score;
    private String level; // WEAK, VERY_WEAK, etc.
}
