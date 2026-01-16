package com.example.passwordpolicyservice.service;

import com.example.passwordpolicyservice.dto.GenerateRequest;
import com.example.passwordpolicyservice.dto.GenerateResponse;
import com.example.passwordpolicyservice.dto.StrengthResponse;
import com.example.passwordpolicyservice.enums.StrengthLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordPolicyServiceTest {

    private final PasswordPolicyService service = new PasswordPolicyService();

    @Test
    void testGeneratePassword() {
        GenerateRequest request = new GenerateRequest();
        request.setLength(16);
        request.setIncludeUpper(true);
        request.setIncludeLower(true);
        request.setIncludeDigits(true);
        request.setIncludeSymbols(true);

        GenerateResponse response = service.generate(request);

        assertNotNull(response.getPassword());
        assertEquals(16, response.getLength());
        assertEquals(16, response.getPassword().length());
    }

    @Test
    void testWeakPassword() {
        StrengthResponse response = service.evaluate("password");
        assertEquals(StrengthLevel.VERY_WEAK, response.getLevel());
        assertTrue(response.getScore() < 20);
    }

    @Test
    void testStrongPassword() {
        StrengthResponse response = service.evaluate("MyS3cr3tP@ssw0rd!");
        assertTrue(response.getScore() >= 60);
        assertTrue(response.getLevel().ordinal() >= StrengthLevel.STRONG.ordinal());
    }
}
