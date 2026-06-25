package com.example.cuenta.infrastructure.adapter.in.rest.dto;

import com.example.cuenta.domain.model.TipoCuenta;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CuentaRequestDTO(

        @NotBlank(message = "El número de cuenta es obligatorio")
        String numeroCuenta,

        @NotNull(message = "El tipo de cuenta es obligatorio")
        TipoCuenta tipoCuenta,

        @NotNull(message = "El saldo inicial es obligatorio")
        @DecimalMin(value = "0.0", message = "El saldo inicial no puede ser negativo")
        @Digits(integer = 13, fraction = 2, message = "El saldo inicial excede el rango permitido (máximo 13 dígitos enteros y 2 decimales)")
        java.math.BigDecimal saldoInicial,

        @NotNull(message = "El estado es obligatorio")
        Boolean estado,

        @NotBlank(message = "El clienteId es obligatorio")
        String clienteId
) {
}
