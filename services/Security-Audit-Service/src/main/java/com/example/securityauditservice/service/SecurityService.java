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
    public Alert checkPassword(String userId, AlertRequest request) {
        log.info("Checking password security for user: {}, item: {}", userId, request.getVaultItemId());

        boolean isWeak = request.getScore() < 50 ||
                "WEAK".equalsIgnoreCase(request.getLevel()) ||
                "VERY_WEAK".equalsIgnoreCase(request.getLevel());

        if (isWeak) {
            return upsertWeakPasswordAlert(userId, request);
        } else {
            resolveExistingWeakPasswordAlert(userId, request.getVaultItemId());
            return null;
        }
    }

    private Alert upsertWeakPasswordAlert(String userId, AlertRequest request) {
        Optional<Alert> existingAlert = alertRepository.findByUserIdAndRelatedItemIdAndType(
                userId, request.getVaultItemId(), AlertType.WEAK_PASSWORD);

        if (existingAlert.isPresent()) {
            Alert alert = existingAlert.get();
            if (alert.getStatus() != AlertStatus.IGNORED) { // Don't reactivate if ignored? Or should we?
                // Requirement says "create or upsert an ACTIVE alert".
                // Usually if it's ignored, we might leave it. But if it gets WORSE or same,
                // maybe we shouldn't bother the user if they ignored it.
                // However, let's strictly follow "create or upsert an ACTIVE alert".
                // If I have an IGNORED alert, and I save the password again and it's still
                // weak,
                // maybe I should keep it IGNORED.
                // But the prompt says "create or upsert an ACTIVE alert".
                // Let's assume if it was resolved, we reactivate it. If it was Active, we
                // update it.
                // If Ignored? Let's keep it Ignored unless explicitly asked to un-ignore.
                // But simpler MVP approach: Make it ACTIVE.
                // "Behavior: If level ... => create or upsert an ACTIVE alert"
                // So I will set it to ACTIVE.
                alert.setStatus(AlertStatus.ACTIVE);
            }
            // Actually, if I ignore a weak password, I probably don't want to be pestered
            // again immediately.
            // But if I change the password and it's STILL weak, maybe I should be notified?
            // Since we don't know if the password CHANGED or if it's just a re-check,
            // Let's safe bet: Update details, but if it is IGNORED, maybe leave it?
            // Let's stick to the prompt text: "create or upsert an ACTIVE alert".
            // This implies ensuring it IS active.
            alert.setStatus(AlertStatus.ACTIVE); // Reactivate it
            alert.setSeverity(determineSeverity(request));
            alert.setDetails("Password score: " + request.getScore() + ", Level: " + request.getLevel());
            alert.setTitle("Weak Password Detected"); // Or update title
            return alertRepository.save(alert);
        } else {
            Alert newAlert = Alert.builder()
                    .userId(userId)
                    .type(AlertType.WEAK_PASSWORD)
                    .severity(determineSeverity(request))
                    .status(AlertStatus.ACTIVE)
                    .title("Weak Password Detected")
                    .details("Password score: " + request.getScore() + ", Level: " + request.getLevel())
                    .relatedItemId(request.getVaultItemId())
                    .build();
            return alertRepository.save(newAlert);
        }
    }

    private void resolveExistingWeakPasswordAlert(String userId, String vaultItemId) {
        alertRepository.findByUserIdAndRelatedItemIdAndType(userId, vaultItemId, AlertType.WEAK_PASSWORD)
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
