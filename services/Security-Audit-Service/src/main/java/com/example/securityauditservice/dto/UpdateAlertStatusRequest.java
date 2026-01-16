package com.example.securityauditservice.dto;

import com.example.securityauditservice.enums.AlertStatus;
import lombok.Data;

@Data
public class UpdateAlertStatusRequest {
    private AlertStatus status;
}
