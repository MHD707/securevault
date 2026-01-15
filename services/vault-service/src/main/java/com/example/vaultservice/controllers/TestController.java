package com.example.vaultservice.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/api/test/token")
    public Map<String, Object> testToken(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "subject", jwt.getSubject(),
                "userId", jwt.getClaim("userId"),
                "scope", jwt.getClaim("scope"),
                "allClaims", jwt.getClaims());
    }
}
