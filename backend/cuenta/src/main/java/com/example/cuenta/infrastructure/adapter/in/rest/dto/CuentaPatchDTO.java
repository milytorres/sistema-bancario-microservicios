package com.example.cuenta.infrastructure.adapter.in.rest.dto;

import com.example.cuenta.domain.model.TipoCuenta;
import lombok.Builder;

@Builder
public record CuentaPatchDTO(
        TipoCuenta tipoCuenta,
        Boolean estado
) {
}
