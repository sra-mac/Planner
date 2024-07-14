package com.rocketseat.camila.planner.activity;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActivitiesDTO(UUID id, String title, LocalDateTime occurs_at) {
}
