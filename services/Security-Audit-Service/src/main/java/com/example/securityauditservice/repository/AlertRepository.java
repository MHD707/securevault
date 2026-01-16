package com.example.securityauditservice.repository;

import com.example.securityauditservice.domain.Alert;
import com.example.securityauditservice.enums.AlertStatus;
import com.example.securityauditservice.enums.AlertType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByUserId(String userId);

    List<Alert> findByUserIdAndStatus(String userId, AlertStatus status);

    Optional<Alert> findByUserIdAndRelatedItemIdAndType(String userId, String relatedItemId, AlertType type);

    long countByUserIdAndStatus(String userId, AlertStatus status);

    long countByUserIdAndStatusAndSeverity(String userId, AlertStatus status,
            com.example.securityauditservice.enums.AlertSeverity severity);

    // For distribution
    long countByUserIdAndStatusAndType(String userId, AlertStatus status, AlertType type);
}
