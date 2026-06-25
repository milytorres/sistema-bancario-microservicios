package com.example.cuenta.infrastructure.adapter.in.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.cuenta.domain.model.Movimiento;
import com.example.cuenta.domain.model.TipoMovimiento;
import com.example.cuenta.domain.port.in.RegistrarMovimientoUseCase;
import com.example.cuenta.infrastructure.adapter.in.rest.dto.MovimientoRequestDTO;
import com.example.cuenta.infrastructure.adapter.in.rest.dto.MovimientoResponseDTO;
import com.example.cuenta.infrastructure.adapter.in.rest.mapper.MovimientoRestMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MovimientoController.class)
class MovimientoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegistrarMovimientoUseCase movimientoUseCase;

    @MockBean
    private MovimientoRestMapper mapper;

    private Movimiento movimientoDominio() {
        return Movimiento.builder()
                .id(1L)
                .fecha(LocalDateTime.now())
                .tipoMovimiento(TipoMovimiento.DEPOSITO)
                .valor(new BigDecimal("500.00"))
                .saldo(new BigDecimal("1500.00"))
                .cuentaId(1L)
                .build();
    }

    private MovimientoResponseDTO responseDTO() {
        return MovimientoResponseDTO.builder()
                .id(1L)
                .fecha(LocalDateTime.now())
                .tipoMovimiento(TipoMovimiento.DEPOSITO)
                .valor(new BigDecimal("500.00"))
                .saldo(new BigDecimal("1500.00"))
                .cuentaId(1L)
                .build();
    }

    @Test
    void debeRegistrarMovimientoYRetornar201() throws Exception {
        MovimientoRequestDTO request = MovimientoRequestDTO.builder()
                .cuentaId(1L)
                .tipoMovimiento(TipoMovimiento.DEPOSITO)
                .valor(new BigDecimal("500.00"))
                .build();

        when(movimientoUseCase.registrar(eq(1L), eq(TipoMovimiento.DEPOSITO), eq(new BigDecimal("500.00"))))
                .thenReturn(movimientoDominio());
        when(mapper.aResponseDTO(any(Movimiento.class))).thenReturn(responseDTO());

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoMovimiento").value("DEPOSITO"))
                .andExpect(jsonPath("$.saldo").value(1500.00));
    }

    @Test
    void debeListarMovimientosPorCuenta() throws Exception {
        when(movimientoUseCase.listarPorCuenta(1L)).thenReturn(List.of(movimientoDominio()));
        when(mapper.aResponseDTO(any(Movimiento.class))).thenReturn(responseDTO());

        mockMvc.perform(get("/movimientos").param("cuentaId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].cuentaId").value(1));
    }

    @Test
    void debeBuscarMovimientoPorId() throws Exception {
        when(movimientoUseCase.buscarPorId(1L)).thenReturn(movimientoDominio());
        when(mapper.aResponseDTO(any(Movimiento.class))).thenReturn(responseDTO());

        mockMvc.perform(get("/movimientos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
