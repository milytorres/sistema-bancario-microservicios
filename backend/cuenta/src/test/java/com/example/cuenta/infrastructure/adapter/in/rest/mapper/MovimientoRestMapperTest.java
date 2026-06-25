package com.example.cuenta.infrastructure.adapter.in.rest.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.cuenta.domain.model.Movimiento;
import com.example.cuenta.domain.model.TipoMovimiento;
import com.example.cuenta.infrastructure.adapter.in.rest.dto.MovimientoResponseDTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class MovimientoRestMapperTest {

    private final MovimientoRestMapper mapper = new MovimientoRestMapper();

    @Test
    void debeMapearMovimientoDominioAResponseDTO() {
        LocalDateTime fecha = LocalDateTime.of(2026, 6, 24, 10, 0);
        Movimiento movimiento = Movimiento.builder()
                .id(10L)
                .fecha(fecha)
                .tipoMovimiento(TipoMovimiento.RETIRO)
                .valor(new BigDecimal("200.00"))
                .saldo(new BigDecimal("300.00"))
                .cuentaId(5L)
                .build();

        MovimientoResponseDTO response = mapper.aResponseDTO(movimiento);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.fecha()).isEqualTo(fecha);
        assertThat(response.tipoMovimiento()).isEqualTo(TipoMovimiento.RETIRO);
        assertThat(response.valor()).isEqualTo(new BigDecimal("200.00"));
        assertThat(response.saldo()).isEqualTo(new BigDecimal("300.00"));
        assertThat(response.cuentaId()).isEqualTo(5L);
    }
}
