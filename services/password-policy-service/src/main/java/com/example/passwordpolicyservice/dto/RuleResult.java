package com.example.passwordpolicyservice.dto;

import com.example.passwordpolicyservice.enums.StrengthRuleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleResult {
    private StrengthRuleType type;
    private boolean passed;
    private String message;
}
