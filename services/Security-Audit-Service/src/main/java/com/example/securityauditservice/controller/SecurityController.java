package com.example.securityauditservice.controller;

import com.example.securityauditservice.domain.Alert;
import com.example.securityauditservice.dto.AlertRequest;
import com.example.securityauditservice.dto.SummaryDto;
import com.example.securityauditservice.dto.UpdateAlertStatusRequest;
import com.example.securityauditservice.enums.AlertStatus;
import com.example.securityauditservice.service.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/security")
@RequiredArgsConstructor
@Tag(name = "Security Audit", description = "Endpoints for security alerts and dashboard")
public class SecurityController {

    private final SecurityService securityService;

    @PostMapping("/check")
    @Operation(summary = "Ingest password evaluation result")
    public ResponseEntity<Map<String, Object>> checkPassword(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody AlertRequest request) {

        String userId = jwt.getSubject();
        List<Alert> alerts = securityService.checkPassword(userId, request);

        Map<String, Object> response = new HashMap<>();
        if (alerts != null && !alerts.isEmpty()) {
            response.put("created", true);
            response.put("alerts", alerts);
        } else {
            response.put("created", false);
            response.put("alerts", List.of());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/mfa-check")
    @Operation(summary = "Report MFA status change")
    public ResponseEntity<Void> checkMfaStatus(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam boolean enabled) {

        String userId = jwt.getSubject();
        securityService.checkMfaStatus(userId, enabled);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/alerts")
    @Operation(summary = "Get alerts for current user")
    public ResponseEntity<List<Alert>> getAlerts(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) AlertStatus status) {

        String userId = jwt.getSubject();
        return ResponseEntity.ok(securityService.getAlerts(userId, status));
    }

    @PatchMapping("/alerts/{id}")
    @Operation(summary = "Update alert status")
    public ResponseEntity<Alert> updateAlertStatus(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long id,
            @RequestBody UpdateAlertStatusRequest request) {

        String userId = jwt.getSubject();
        return ResponseEntity.ok(securityService.updateAlertStatus(userId, id, request));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get security dashboard summary")
    public ResponseEntity<SummaryDto> getSummary(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(securityService.getSummary(userId));
    }
}
