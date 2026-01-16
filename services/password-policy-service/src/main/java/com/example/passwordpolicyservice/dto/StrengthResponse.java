package com.example.passwordpolicyservice.dto;

import com.example.passwordpolicyservice.enums.StrengthLevel;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class StrengthResponse {
    private int score; // 0-100
    private StrengthLevel level;
    private String label;
    private double progress; // 0.0 - 1.0
    private List<String> warnings;
    private List<RuleResult> rules;
}
