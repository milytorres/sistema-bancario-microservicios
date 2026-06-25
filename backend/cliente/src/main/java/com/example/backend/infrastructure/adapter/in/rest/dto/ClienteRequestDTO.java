package com.example.backend.infrastructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ClienteRequestDTO(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120)
        String nombre,

        @Size(max = 20)
        String genero,

        @Positive(message = "La edad debe ser un valor positivo")
        Integer edad,

        @NotBlank(message = "La identificación es obligatoria")
        @Size(max = 30)
        String identificacion,

        @Size(max = 200)
        String direccion,

        @Size(max = 20)
        String telefono,

        @NotBlank(message = "El clienteId es obligatorio")
        @Size(max = 50)
        String clienteId,

        @NotBlank(message = "La contraseña es obligatoria")
        String contrasena,

        @NotNull(message = "El estado es obligatorio")
        Boolean estado
) {
}
