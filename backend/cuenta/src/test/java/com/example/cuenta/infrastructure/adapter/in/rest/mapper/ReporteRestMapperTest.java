package com.example.cuenta.infrastructure.adapter.in.rest.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.cuenta.domain.model.ReporteMovimiento;
import com.example.cuenta.domain.model.TipoCuenta;
import com.example.cuenta.infrastructure.adapter.in.rest.dto.ReporteMovimientoResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ReporteRestMapperTest {

    private final ReporteRestMapper mapper = new ReporteRestMapper();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @Test
    void debeMapearReporteDominioAResponseDTO() {
        LocalDateTime fecha = LocalDateTime.of(2026, 6, 24, 9, 0);
        ReporteMovimiento reporte = ReporteMovimiento.builder()
                .fecha(fecha)
                .cliente("CLI001")
                .numeroCuenta("478758")
                .tipoCuenta(TipoCuenta.AHORRO)
                .saldoInicial(new BigDecimal("1000.00"))
                .estado(true)
                .movimiento(new BigDecimal("500.00"))
                .saldoDisponible(new BigDecimal("1500.00"))
                .build();

        ReporteMovimientoResponseDTO response = mapper.aResponseDTO(reporte);

        assertThat(response.fecha()).isEqualTo(fecha);
        assertThat(response.cliente()).isEqualTo("CLI001");
        assertThat(response.numeroCuenta()).isEqualTo("478758");
        assertThat(response.tipo()).isEqualTo("AHORRO");
        assertThat(response.saldoInicial()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(response.estado()).isTrue();
        assertThat(response.movimiento()).isEqualTo(new BigDecimal("500.00"));
        assertThat(response.saldoDisponible()).isEqualTo(new BigDecimal("1500.00"));
    }

    @Test
    void debeSerializarConClavesJsonExactasDelPdf() throws Exception {
        ReporteMovimiento reporte = ReporteMovimiento.builder()
                .fecha(LocalDateTime.of(2026, 6, 24, 9, 0))
                .cliente("CLI001")
                .numeroCuenta("478758")
                .tipoCuenta(TipoCuenta.CORRIENTE)
                .saldoInicial(new BigDecimal("1000.00"))
                .estado(true)
                .movimiento(new BigDecimal("-500.00"))
                .saldoDisponible(new BigDecimal("500.00"))
                .build();

        ReporteMovimientoResponseDTO response = mapper.aResponseDTO(reporte);

        String json = objectMapper.writeValueAsString(response);

        assertThat(json).contains("\"Fecha\"");
        assertThat(json).contains("\"Cliente\"");
        assertThat(json).contains("\"Numero Cuenta\"");
        assertThat(json).contains("\"Tipo\"");
        assertThat(json).contains("\"Saldo Inicial\"");
        assertThat(json).contains("\"Estado\"");
        assertThat(json).contains("\"Movimiento\"");
        assertThat(json).contains("\"Saldo Disponible\"");
    }
}
