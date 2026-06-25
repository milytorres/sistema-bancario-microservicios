package com.example.backend.infrastructure.adapter.in.rest.dto;

import lombok.Builder;

@Builder
public record ClienteResponseDTO(
        Long id,
        String nombre,
        String genero,
        Integer edad,
        String identificacion,
        String direccion,
        String telefono,
        String clienteId,
        Boolean estado
) {
}
