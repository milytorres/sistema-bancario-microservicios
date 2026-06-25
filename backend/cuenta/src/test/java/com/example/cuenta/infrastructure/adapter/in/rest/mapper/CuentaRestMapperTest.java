package com.example.cuenta.infrastructure.adapter.in.rest.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.cuenta.domain.model.Cuenta;
import com.example.cuenta.domain.model.TipoCuenta;
import com.example.cuenta.infrastructure.adapter.in.rest.dto.CuentaPatchDTO;
import com.example.cuenta.infrastructure.adapter.in.rest.dto.CuentaRequestDTO;
import com.example.cuenta.infrastructure.adapter.in.rest.dto.CuentaResponseDTO;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CuentaRestMapperTest {

    private final CuentaRestMapper mapper = new CuentaRestMapper();

    @Test
    void debeMapearCuentaRequestDTOADominio() {
        CuentaRequestDTO request = CuentaRequestDTO.builder()
                .numeroCuenta("478758")
                .tipoCuenta(TipoCuenta.AHORRO)
                .saldoInicial(new BigDecimal("1000.00"))
                .estado(true)
                .clienteId("CLI001")
                .build();

        Cuenta cuenta = mapper.aDominio(request);

        assertThat(cuenta.getNumeroCuenta()).isEqualTo("478758");
        assertThat(cuenta.getTipoCuenta()).isEqualTo(TipoCuenta.AHORRO);
        assertThat(cuenta.getSaldoInicial()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(cuenta.getEstado()).isTrue();
        assertThat(cuenta.getClienteId()).isEqualTo("CLI001");
    }

    @Test
    void debeMapearCuentaPatchDTOADominioSoloConCamposParciales() {
        CuentaPatchDTO patch = CuentaPatchDTO.builder()
                .tipoCuenta(TipoCuenta.CORRIENTE)
                .estado(false)
                .build();

        Cuenta cuenta = mapper.aDominio(patch);

        assertThat(cuenta.getTipoCuenta()).isEqualTo(TipoCuenta.CORRIENTE);
        assertThat(cuenta.getEstado()).isFalse();
        assertThat(cuenta.getNumeroCuenta()).isNull();
        assertThat(cuenta.getClienteId()).isNull();
    }

    @Test
    void debeMapearDominioAResponseDTOConSaldos() {
        Cuenta cuenta = Cuenta.builder()
                .id(1L)
                .numeroCuenta("478758")
                .tipoCuenta(TipoCuenta.AHORRO)
                .saldoInicial(new BigDecimal("1000.00"))
                .saldoDisponible(new BigDecimal("850.00"))
                .estado(true)
                .clienteId("CLI001")
                .build();

        CuentaResponseDTO response = mapper.aResponseDTO(cuenta);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.numeroCuenta()).isEqualTo("478758");
        assertThat(response.saldoInicial()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(response.saldoDisponible()).isEqualTo(new BigDecimal("850.00"));
        assertThat(response.estado()).isTrue();
        assertThat(response.clienteId()).isEqualTo("CLI001");
    }
}
