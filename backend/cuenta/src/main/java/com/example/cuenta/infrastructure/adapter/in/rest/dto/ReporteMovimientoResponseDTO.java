package com.example.cuenta.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ReporteMovimientoResponseDTO(

        @JsonProperty("Fecha")
        LocalDateTime fecha,

        @JsonProperty("Cliente")
        String cliente,

        @JsonProperty("Numero Cuenta")
        String numeroCuenta,

        @JsonProperty("Tipo")
        String tipo,

        @JsonProperty("Saldo Inicial")
        BigDecimal saldoInicial,

        @JsonProperty("Estado")
        Boolean estado,

        @JsonProperty("Movimiento")
        BigDecimal movimiento,

        @JsonProperty("Saldo Disponible")
        BigDecimal saldoDisponible
) {
}
