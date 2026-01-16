package com.example.securityauditservice.service;

import com.example.securityauditservice.domain.Alert;
import com.example.securityauditservice.dto.AlertRequest;
import com.example.securityauditservice.dto.SummaryDto;
import com.example.securityauditservice.dto.UpdateAlertStatusRequest;
import com.example.securityauditservice.enums.AlertSeverity;
import com.example.securityauditservice.enums.AlertStatus;
import com.example.securityauditservice.enums.AlertType;
import com.example.securityauditservice.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityService {

    private final AlertRepository alertRepository;

    @Transactional
    public List<Alert> checkPassword(String userId, AlertRequest request) {
        log.info("Checking password security for user: {}, item: {}", userId, request.getVaultItemId());
        List<Alert> createdOrUpdatedAlerts = new java.util.ArrayList<>();

        // 1. Weak Password Check
        boolean isWeak = request.getScore() < 50 ||
                "WEAK".equalsIgnoreCase(request.getLevel()) ||
                "VERY_WEAK".equalsIgnoreCase(request.getLevel());

        if (isWeak) {
            createdOrUpdatedAlerts.add(upsertAlert(userId, request.getVaultItemId(), AlertType.WEAK_PASSWORD,
                    determineSeverity(request), "Weak Password Detected",
                    "Password score: " + request.getScore() + ", Level: " + request.getLevel()));
        } else {
            resolveExistingAlert(userId, request.getVaultItemId(), AlertType.WEAK_PASSWORD);
        }

        // 2. Reused Password Check
        if (request.isReused()) {
            createdOrUpdatedAlerts.add(upsertAlert(userId, request.getVaultItemId(), AlertType.REUSED_PASSWORD,
                    AlertSeverity.MEDIUM, "Reused Password Detected",
                    "This password has been used before. Please use unique passwords."));
        } else {
            resolveExistingAlert(userId, request.getVaultItemId(), AlertType.REUSED_PASSWORD);
        }

        // 3. Compromised Password Check
        if (request.isCompromised()) {
            createdOrUpdatedAlerts.add(upsertAlert(userId, request.getVaultItemId(), AlertType.COMPROMISED_PASSWORD,
                    AlertSeverity.HIGH, "Compromised Password Detected",
                    "This password appears in a known data breach. Change it immediately."));
        } else {
            resolveExistingAlert(userId, request.getVaultItemId(), AlertType.COMPROMISED_PASSWORD);
        }

        return createdOrUpdatedAlerts;
    }

    @Transactional
    public void checkMfaStatus(String userId, boolean isEnabled) {
        if (!isEnabled) {
            upsertAlert(userId, "MFA_STATUS", AlertType.MFA_DISABLED, AlertSeverity.HIGH,
                    "MFA Disabled", "Multi-Factor Authentication is disabled. Enable it for better security.");
        } else {
            resolveExistingAlert(userId, "MFA_STATUS", AlertType.MFA_DISABLED);
        }
    }

    private Alert upsertAlert(String userId, String relatedItemId, AlertType type, AlertSeverity severity, String title,
            String details) {
        Optional<Alert> existingAlert = alertRepository.findByUserIdAndRelatedItemIdAndType(userId, relatedItemId,
                type);

        if (existingAlert.isPresent()) {
            Alert alert = existingAlert.get();
            if (alert.getStatus() != AlertStatus.IGNORED) {
                alert.setStatus(AlertStatus.ACTIVE);
            }
            alert.setSeverity(severity); // Update severity if it changes
            alert.setDetails(details);
            alert.setTitle(title);
            return alertRepository.save(alert);
        } else {
            Alert newAlert = Alert.builder()
                    .userId(userId)
                    .type(type)
                    .severity(severity)
                    .status(AlertStatus.ACTIVE)
                    .title(title)
                    .details(details)
                    .relatedItemId(relatedItemId)
                    .build();
            return alertRepository.save(newAlert);
        }
    }

    private void resolveExistingAlert(String userId, String relatedItemId, AlertType type) {
        alertRepository.findByUserIdAndRelatedItemIdAndType(userId, relatedItemId, type)
                .ifPresent(alert -> {
                    if (alert.getStatus() == AlertStatus.ACTIVE) {
                        alert.setStatus(AlertStatus.RESOLVED);
                        alertRepository.save(alert);
                    }
                });
    }

    private AlertSeverity determineSeverity(AlertRequest request) {
        if (request.getScore() < 30 || "VERY_WEAK".equalsIgnoreCase(request.getLevel())) {
            return AlertSeverity.HIGH;
        }
        return AlertSeverity.MEDIUM;
    }

    public List<Alert> getAlerts(String userId, AlertStatus status) {
        if (status != null) {
            return alertRepository.findByUserIdAndStatus(userId, status);
        }
        return alertRepository.findByUserId(userId);
    }

    @Transactional
    public Alert updateAlertStatus(String userId, Long alertId, UpdateAlertStatusRequest request) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found"));

        if (!alert.getUserId().equals(userId)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You are not authorized to update this alert");
        }

        alert.setStatus(request.getStatus());
        return alertRepository.save(alert);
    }

    public SummaryDto getSummary(String userId) {
        long active = alertRepository.countByUserIdAndStatus(userId, AlertStatus.ACTIVE);
        long weak = alertRepository.countByUserIdAndStatusAndType(userId, AlertStatus.ACTIVE, AlertType.WEAK_PASSWORD);

        Map<AlertStatus, Long> distribution = new HashMap<>();
        distribution.put(AlertStatus.ACTIVE, active);
        distribution.put(AlertStatus.RESOLVED, alertRepository.countByUserIdAndStatus(userId, AlertStatus.RESOLVED));
        distribution.put(AlertStatus.IGNORED, alertRepository.countByUserIdAndStatus(userId, AlertStatus.IGNORED));

        return SummaryDto.builder()
                .activeAlerts(active)
                .weakAlerts(weak)
                .distribution(distribution)
                .build();
    }
}
