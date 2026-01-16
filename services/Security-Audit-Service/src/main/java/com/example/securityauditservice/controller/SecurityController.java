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
        Alert alert = securityService.checkPassword(userId, request);

        Map<String, Object> response = new HashMap<>();
        if (alert != null) {
            response.put("created", true);
            response.put("alert", alert);
        } else {
            response.put("created", false);
            response.put("alert", null);
        }
        return ResponseEntity.ok(response);
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
