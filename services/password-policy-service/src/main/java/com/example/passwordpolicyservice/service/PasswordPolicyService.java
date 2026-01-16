package com.example.passwordpolicyservice.service;

import com.example.passwordpolicyservice.dto.*;
import com.example.passwordpolicyservice.enums.StrengthLevel;
import com.example.passwordpolicyservice.enums.StrengthRuleType;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class PasswordPolicyService {

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+[]{}|;:,.<>?";
    private static final String AMBIGUOUS = "iIl1Lo0O";

    private final SecureRandom random = new SecureRandom();

    public PasswordPolicy getPolicy() {
        return PasswordPolicy.builder()
                .minLength(12)
                .recommendedLength(16)
                .requireUpper(true)
                .requireLower(true)
                .requireDigits(true)
                .requireSymbols(true)
                .build();
    }

    public GenerateResponse generate(GenerateRequest request) {
        StringBuilder password = new StringBuilder();
        List<String> charCategories = new ArrayList<>();

        String lower = request.isExcludeAmbiguous() ? removeAmbiguous(LOWERCASE) : LOWERCASE;
        String upper = request.isExcludeAmbiguous() ? removeAmbiguous(UPPERCASE) : UPPERCASE;
        String digits = request.isExcludeAmbiguous() ? removeAmbiguous(DIGITS) : DIGITS;
        String symbols = request.isExcludeAmbiguous() ? removeAmbiguous(SYMBOLS) : SYMBOLS;

        if (request.isIncludeLower())
            charCategories.add(lower);
        if (request.isIncludeUpper())
            charCategories.add(upper);
        if (request.isIncludeDigits())
            charCategories.add(digits);
        if (request.isIncludeSymbols())
            charCategories.add(symbols);

        if (charCategories.isEmpty()) {
            charCategories.add(LOWERCASE); // Default fallback
        }

        // Ensure at least one char from each selected category
        for (String category : charCategories) {
            password.append(category.charAt(random.nextInt(category.length())));
        }

        // Fill the rest
        String allChars = String.join("", charCategories);
        while (password.length() < request.getLength()) {
            char nextChar = allChars.charAt(random.nextInt(allChars.length()));
            if (!request.isExcludeSimilar() || password.indexOf(String.valueOf(nextChar)) == -1) {
                password.append(nextChar);
            }
        }

        return new GenerateResponse(shuffleString(password.toString()), password.length());
    }

    public StrengthResponse evaluate(String password) {
        if (password == null)
            password = "";

        int score = 0;
        List<RuleResult> rules = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // 1. Length Check
        boolean lenCheck = password.length() >= 12;
        rules.add(new RuleResult(StrengthRuleType.MIN_LENGTH, lenCheck, "Length should be at least 12 characters"));
        if (lenCheck)
            score += 10;
        if (password.length() >= 16)
            score += 10;
        score += Math.min(password.length() * 2, 20); // Up to 20 points for length

        // 2. Character Categories
        boolean hasUpper = Pattern.compile("[A-Z]").matcher(password).find();
        rules.add(new RuleResult(StrengthRuleType.HAS_UPPER, hasUpper, "Include uppercase letters"));
        if (hasUpper)
            score += 10;

        boolean hasLower = Pattern.compile("[a-z]").matcher(password).find();
        rules.add(new RuleResult(StrengthRuleType.HAS_LOWER, hasLower, "Include lowercase letters"));
        if (hasLower)
            score += 10;

        boolean hasDigit = Pattern.compile("[0-9]").matcher(password).find();
        rules.add(new RuleResult(StrengthRuleType.HAS_DIGIT, hasDigit, "Include numbers"));
        if (hasDigit)
            score += 10;

        boolean hasSymbol = Pattern.compile("[^a-zA-Z0-9]").matcher(password).find();
        rules.add(new RuleResult(StrengthRuleType.HAS_SYMBOL, hasSymbol, "Include special symbols"));
        if (hasSymbol)
            score += 10;

        // 3. Patterns
        if (Pattern.compile("123|abc|qwerty|password|admin").matcher(password.toLowerCase()).find()) {
            score -= 20;
            warnings.add("Avoid common sequences and words");
            rules.add(new RuleResult(StrengthRuleType.NO_COMMON_PATTERN, false, "Avoid common patterns"));
        } else {
            rules.add(new RuleResult(StrengthRuleType.NO_COMMON_PATTERN, true, "No common patterns found"));
        }

        if (hasRepeatedChars(password)) {
            score -= 10;
            warnings.add("Avoid repeated characters");
            rules.add(new RuleResult(StrengthRuleType.NO_REPEAT_CHARS, false, "Avoid repeated characters"));
        } else {
            rules.add(new RuleResult(StrengthRuleType.NO_REPEAT_CHARS, true, "No repeated characters found"));
        }

        // Normalize Score
        if (score < 0)
            score = 0;
        if (score > 100)
            score = 100;

        return StrengthResponse.builder()
                .score(score)
                .level(determineLevel(score))
                .label(determineLevel(score).name())
                .progress(score / 100.0)
                .warnings(warnings)
                .rules(rules)
                .build();
    }

    private StrengthLevel determineLevel(int score) {
        if (score < 20)
            return StrengthLevel.VERY_WEAK;
        if (score < 40)
            return StrengthLevel.WEAK;
        if (score < 60)
            return StrengthLevel.MEDIUM;
        if (score < 80)
            return StrengthLevel.STRONG;
        return StrengthLevel.VERY_STRONG;
    }

    private String removeAmbiguous(String source) {
        StringBuilder sb = new StringBuilder();
        for (char c : source.toCharArray()) {
            if (AMBIGUOUS.indexOf(c) == -1) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private String shuffleString(String input) {
        List<Character> characters = new ArrayList<>();
        for (char c : input.toCharArray()) {
            characters.add(c);
        }
        Collections.shuffle(characters);
        StringBuilder result = new StringBuilder();
        for (Character c : characters) {
            result.append(c);
        }
        return result.toString();
    }

    private boolean hasRepeatedChars(String password) {
        // Simple check for 3+ repeated chars like "aaa"
        return Pattern.compile("(.)\\1\\1").matcher(password).find();
    }
}
