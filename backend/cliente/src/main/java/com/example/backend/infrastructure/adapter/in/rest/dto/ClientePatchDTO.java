package com.example.backend.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ClientePatchDTO(

        @Size(max = 120)
        String nombre,

        @Size(max = 20)
        String genero,

        @Positive(message = "La edad debe ser un valor positivo")
        Integer edad,

        @Size(max = 30)
        String identificacion,

        @Size(max = 200)
        String direccion,

        @Size(max = 20)
        String telefono,

        String contrasena,

        Boolean estado
) {
}
