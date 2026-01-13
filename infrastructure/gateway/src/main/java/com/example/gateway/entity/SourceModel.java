package com.example.gateway.entity;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SourceModel {
    private UUID id;
    private SourceType type;
    private String uri;
    private String label;
    private Instant createdAt;
}
