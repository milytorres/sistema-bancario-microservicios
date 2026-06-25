package com.example.cuenta.infrastructure.adapter.out.client.dto;

public record ClienteHttpResponse(
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
