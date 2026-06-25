package com.example.backend.infrastructure.adapter.in.rest.dto;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Builder;

@Builder
public record ErrorResponseDTO(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> errores
) {
}
