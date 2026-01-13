package com.example.gateway.entity;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectModel {

    private UUID id;
    private UUID hackathonId;
    private UUID groupId;
    private String title;
    private String description;
    private ProjectStatus status;
    private Instant createdAt;
    private Instant updatedAt;
    private Long userId;

}
