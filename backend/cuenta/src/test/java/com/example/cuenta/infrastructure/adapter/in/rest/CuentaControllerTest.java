package com.example.cuenta.infrastructure.adapter.in.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.cuenta.domain.model.Cuenta;
import com.example.cuenta.domain.model.TipoCuenta;
import com.example.cuenta.domain.port.in.GestionarCuentaUseCase;
import com.example.cuenta.infrastructure.adapter.in.rest.dto.CuentaPatchDTO;
import com.example.cuenta.infrastructure.adapter.in.rest.dto.CuentaRequestDTO;
import com.example.cuenta.infrastructure.adapter.in.rest.dto.CuentaResponseDTO;
import com.example.cuenta.infrastructure.adapter.in.rest.mapper.CuentaRestMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CuentaController.class)
class CuentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GestionarCuentaUseCase cuentaUseCase;

    @MockBean
    private CuentaRestMapper mapper;

    private Cuenta cuentaDominio() {
        return Cuenta.builder()
                .id(1L)
                .numeroCuenta("478758")
                .tipoCuenta(TipoCuenta.AHORRO)
                .saldoInicial(new BigDecimal("1000.00"))
                .saldoDisponible(new BigDecimal("1000.00"))
                .estado(true)
                .clienteId("CLI001")
                .build();
    }

    private CuentaResponseDTO responseDTO() {
        return CuentaResponseDTO.builder()
                .id(1L)
                .numeroCuenta("478758")
                .tipoCuenta(TipoCuenta.AHORRO)
                .saldoInicial(new BigDecimal("1000.00"))
                .saldoDisponible(new BigDecimal("1000.00"))
                .estado(true)
                .clienteId("CLI001")
                .build();
    }

    @Test
    void debeListarTodasLasCuentas() throws Exception {
        when(cuentaUseCase.listarTodas()).thenReturn(List.of(cuentaDominio()));
        when(mapper.aResponseDTO(any(Cuenta.class))).thenReturn(responseDTO());

        mockMvc.perform(get("/cuentas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].numeroCuenta").value("478758"));
    }

    @Test
    void debeBuscarCuentaPorId() throws Exception {
        when(cuentaUseCase.buscarPorId(1L)).thenReturn(cuentaDominio());
        when(mapper.aResponseDTO(any(Cuenta.class))).thenReturn(responseDTO());

        mockMvc.perform(get("/cuentas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void debeCrearCuentaYRetornar201() throws Exception {
        CuentaRequestDTO request = CuentaRequestDTO.builder()
                .numeroCuenta("478758")
                .tipoCuenta(TipoCuenta.AHORRO)
                .saldoInicial(new BigDecimal("1000.00"))
                .estado(true)
                .clienteId("CLI001")
                .build();

        when(mapper.aDominio(any(CuentaRequestDTO.class))).thenReturn(cuentaDominio());
        when(cuentaUseCase.crear(any(Cuenta.class))).thenReturn(cuentaDominio());
        when(mapper.aResponseDTO(any(Cuenta.class))).thenReturn(responseDTO());

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCuenta").value("478758"));
    }

    @Test
    void debeActualizarCuentaCompleta() throws Exception {
        CuentaRequestDTO request = CuentaRequestDTO.builder()
                .numeroCuenta("478758")
                .tipoCuenta(TipoCuenta.CORRIENTE)
                .saldoInicial(new BigDecimal("2000.00"))
                .estado(true)
                .clienteId("CLI001")
                .build();

        when(mapper.aDominio(any(CuentaRequestDTO.class))).thenReturn(cuentaDominio());
        when(cuentaUseCase.actualizar(eq(1L), any(Cuenta.class))).thenReturn(cuentaDominio());
        when(mapper.aResponseDTO(any(Cuenta.class))).thenReturn(responseDTO());

        mockMvc.perform(put("/cuentas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void debeActualizarCuentaParcialmente() throws Exception {
        CuentaPatchDTO request = CuentaPatchDTO.builder()
                .estado(false)
                .build();

        when(mapper.aDominio(any(CuentaPatchDTO.class))).thenReturn(cuentaDominio());
        when(cuentaUseCase.actualizarParcial(eq(1L), any(Cuenta.class))).thenReturn(cuentaDominio());
        when(mapper.aResponseDTO(any(Cuenta.class))).thenReturn(responseDTO());

        mockMvc.perform(patch("/cuentas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void debeEliminarCuentaYRetornar204() throws Exception {
        mockMvc.perform(delete("/cuentas/1"))
                .andExpect(status().isNoContent());

        verify(cuentaUseCase).eliminar(1L);
    }
}
