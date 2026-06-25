package com.example.cuenta.infrastructure.adapter.in.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.cuenta.domain.model.ReporteMovimiento;
import com.example.cuenta.domain.model.TipoCuenta;
import com.example.cuenta.domain.port.in.GenerarReporteUseCase;
import com.example.cuenta.infrastructure.adapter.in.rest.dto.ReporteMovimientoResponseDTO;
import com.example.cuenta.infrastructure.adapter.in.rest.mapper.ReporteRestMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReporteController.class)
class ReporteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenerarReporteUseCase reporteUseCase;

    @MockBean
    private ReporteRestMapper mapper;

    private ReporteMovimiento reporteDominio() {
        return ReporteMovimiento.builder()
                .fecha(LocalDateTime.now())
                .cliente("CLI001")
                .numeroCuenta("478758")
                .tipoCuenta(TipoCuenta.AHORRO)
                .saldoInicial(new BigDecimal("1000.00"))
                .estado(true)
                .movimiento(new BigDecimal("500.00"))
                .saldoDisponible(new BigDecimal("1500.00"))
                .build();
    }

    private ReporteMovimientoResponseDTO responseDTO() {
        return ReporteMovimientoResponseDTO.builder()
                .fecha(LocalDateTime.now())
                .cliente("CLI001")
                .numeroCuenta("478758")
                .tipo("AHORRO")
                .saldoInicial(new BigDecimal("1000.00"))
                .estado(true)
                .movimiento(new BigDecimal("500.00"))
                .saldoDisponible(new BigDecimal("1500.00"))
                .build();
    }

    @Test
    void debeGenerarReporteConFechaYClienteValidos() throws Exception {
        when(mapper.parsearRangoFechas("2026-06-01,2026-06-24"))
                .thenReturn(new LocalDate[] {LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 24)});
        when(reporteUseCase.generar(eq("CLI001"), eq(LocalDate.of(2026, 6, 1)), eq(LocalDate.of(2026, 6, 24))))
                .thenReturn(List.of(reporteDominio()));
        when(mapper.aResponseDTO(any(ReporteMovimiento.class))).thenReturn(responseDTO());

        mockMvc.perform(get("/reportes")
                        .param("fecha", "2026-06-01,2026-06-24")
                        .param("cliente", "CLI001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].Cliente").value("CLI001"))
                .andExpect(jsonPath("$[0]['Numero Cuenta']").value("478758"))
                .andExpect(jsonPath("$[0]['Saldo Disponible']").value(1500.00));
    }
}
