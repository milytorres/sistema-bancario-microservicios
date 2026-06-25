package com.example.cuenta.infrastructure.adapter.in.rest.dto;

import com.example.cuenta.domain.model.TipoMovimiento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record MovimientoRequestDTO(

        @NotNull(message = "El id de la cuenta es obligatorio")
        Long cuentaId,

        @NotNull(message = "El tipo de movimiento es obligatorio")
        TipoMovimiento tipoMovimiento,

        @NotNull(message = "El valor es obligatorio")
        @DecimalMin(value = "0.01", message = "El valor debe ser mayor a cero")
        @Digits(integer = 13, fraction = 2, message = "El valor excede el rango permitido (máximo 13 dígitos enteros y 2 decimales)")
        BigDecimal valor
) {
}
