package com.example.cuenta.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class CuentaTest {

    private Cuenta crearCuenta(BigDecimal saldoDisponible) {
        return Cuenta.builder()
                .id(1L)
                .numeroCuenta("001-000001")
                .tipoCuenta(TipoCuenta.AHORRO)
                .saldoInicial(new BigDecimal("1000.00"))
                .saldoDisponible(saldoDisponible)
                .estado(true)
                .clienteId("CLI001")
                .build();
    }

    @Test
    void debeTenerSaldoSuficienteCuandoElMontoEsMenorQueElSaldoDisponible() {
        Cuenta cuenta = crearCuenta(new BigDecimal("500.00"));

        assertThat(cuenta.tieneSaldoSuficiente(new BigDecimal("200.00"))).isTrue();
    }

    @Test
    void debeTenerSaldoSuficienteCuandoElMontoEsIgualAlSaldoDisponible() {
        Cuenta cuenta = crearCuenta(new BigDecimal("500.00"));

        assertThat(cuenta.tieneSaldoSuficiente(new BigDecimal("500.00"))).isTrue();
    }

    @Test
    void noDebeTenerSaldoSuficienteCuandoElMontoSuperaElSaldoDisponible() {
        Cuenta cuenta = crearCuenta(new BigDecimal("100.00"));

        assertThat(cuenta.tieneSaldoSuficiente(new BigDecimal("200.00"))).isFalse();
    }

    @Test
    void debeIncrementarSaldoDisponibleAlAplicarUnDeposito() {
        Cuenta cuenta = crearCuenta(new BigDecimal("500.00"));

        Cuenta actualizada = cuenta.aplicarMovimiento(new BigDecimal("150.00"));

        assertThat(actualizada.getSaldoDisponible()).isEqualByComparingTo("650.00");
        // La cuenta original es inmutable (builder genera una nueva instancia).
        assertThat(cuenta.getSaldoDisponible()).isEqualByComparingTo("500.00");
    }

    @Test
    void debeDecrementarSaldoDisponibleAlAplicarUnRetiroConValorNegativo() {
        Cuenta cuenta = crearCuenta(new BigDecimal("500.00"));

        // Por convención del dominio (F2), el retiro se aplica como valor negativo.
        Cuenta actualizada = cuenta.aplicarMovimiento(new BigDecimal("-200.00"));

        assertThat(actualizada.getSaldoDisponible()).isEqualByComparingTo("300.00");
    }
}
