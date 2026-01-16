package com.example.securityauditservice.dto;

import com.example.securityauditservice.enums.AlertStatus;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class SummaryDto {
    private long activeAlerts;
    private long weakAlerts; // Active WEAK_PASSWORD alerts
    private Map<AlertStatus, Long> distribution;
}
