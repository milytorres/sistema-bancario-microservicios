package com.example.cuenta.infrastructure.adapter.in.rest.dto;

import com.example.cuenta.domain.model.TipoCuenta;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record CuentaResponseDTO(
        Long id,
        String numeroCuenta,
        TipoCuenta tipoCuenta,
        BigDecimal saldoInicial,
        BigDecimal saldoDisponible,
        Boolean estado,
        String clienteId
) {
}
