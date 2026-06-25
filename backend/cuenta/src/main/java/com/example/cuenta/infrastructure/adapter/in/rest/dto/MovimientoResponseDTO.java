package com.example.cuenta.infrastructure.adapter.in.rest.dto;

import com.example.cuenta.domain.model.TipoMovimiento;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record MovimientoResponseDTO(
        Long id,
        LocalDateTime fecha,
        TipoMovimiento tipoMovimiento,
        BigDecimal valor,
        BigDecimal saldo,
        Long cuentaId
) {
}
