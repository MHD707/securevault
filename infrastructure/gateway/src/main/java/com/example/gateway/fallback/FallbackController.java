package com.example.gateway.fallback;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class FallbackController {
    @GetMapping("/fallback/auth")
    public Map<String, Object> authFallback() {
        return Map.of(
                "message", "Auth service indisponible (fallback). Veuillez réessayer plus tard.",
                "user", Map.of(
                        "id", -1,
                        "email", "unknown@fallback.com",
                        "role", "N/A"
                )
        );
    }
}
