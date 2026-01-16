package com.example.passwordpolicyservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordPolicy {
    private int minLength;
    private int recommendedLength;
    private boolean requireUpper;
    private boolean requireLower;
    private boolean requireDigits;
    private boolean requireSymbols;
}
