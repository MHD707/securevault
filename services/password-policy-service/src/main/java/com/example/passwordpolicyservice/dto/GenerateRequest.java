package com.example.passwordpolicyservice.dto;

import lombok.Data;

@Data
public class GenerateRequest {
    private int length = 12;
    private boolean includeUpper = true;
    private boolean includeLower = true;
    private boolean includeDigits = true;
    private boolean includeSymbols = true;
    private boolean excludeSimilar = false;
    private boolean excludeAmbiguous = false;
}
