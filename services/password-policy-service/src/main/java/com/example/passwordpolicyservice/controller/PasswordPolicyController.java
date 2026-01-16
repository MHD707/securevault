package com.example.passwordpolicyservice.controller;

import com.example.passwordpolicyservice.dto.*;
import com.example.passwordpolicyservice.service.PasswordPolicyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
@Tag(name = "Password Policy", description = "Endpoints for password policy, generation, and strength evaluation")
public class PasswordPolicyController {

    private final PasswordPolicyService passwordPolicyService;

    @GetMapping("/policy")
    @Operation(summary = "Get Password Policy", description = "Returns the current password policy requirements (Public)")
    public ResponseEntity<PasswordPolicy> getPolicy() {
        return ResponseEntity.ok(passwordPolicyService.getPolicy());
    }

    @PostMapping("/generate")
    @Operation(summary = "Generate Password", description = "Generates a random password based on criteria (Authenticated)")
    public ResponseEntity<GenerateResponse> generate(@RequestBody @Valid GenerateRequest request) {
        return ResponseEntity.ok(passwordPolicyService.generate(request));
    }

    @PostMapping("/strength")
    @Operation(summary = "Evaluate Strength", description = "Evaluates the strength of a given password (Authenticated)")
    public ResponseEntity<StrengthResponse> evaluateStrength(@RequestBody StrengthRequest request) {
        return ResponseEntity.ok(passwordPolicyService.evaluate(request.getPassword()));
    }
}
