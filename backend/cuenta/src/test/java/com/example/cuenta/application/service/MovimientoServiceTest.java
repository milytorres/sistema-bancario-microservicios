package com.example.cuenta.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.cuenta.domain.exception.SaldoNoDisponibleException;
import com.example.cuenta.domain.model.Cuenta;
import com.example.cuenta.domain.model.Movimiento;
import com.example.cuenta.domain.model.TipoCuenta;
import com.example.cuenta.domain.model.TipoMovimiento;
import com.example.cuenta.domain.port.out.CuentaRepositoryPort;
import com.example.cuenta.domain.port.out.MovimientoRepositoryPort;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

    @Mock
    private MovimientoRepositoryPort movimientoRepositoryPort;

    @Mock
    private CuentaRepositoryPort cuentaRepositoryPort;

    @InjectMocks
    private MovimientoService movimientoService;

    private Cuenta cuentaConSaldo(BigDecimal saldo) {
        return Cuenta.builder()
                .id(1L)
                .numeroCuenta("001-000001")
                .tipoCuenta(TipoCuenta.AHORRO)
                .saldoInicial(new BigDecimal("1000.00"))
                .saldoDisponible(saldo)
                .estado(true)
                .clienteId("CLI001")
                .build();
    }

    @Test
    void debeLanzarExcepcionSaldoNoDisponibleSiRetiroSuperaElSaldo() {
        Cuenta cuenta = cuentaConSaldo(new BigDecimal("100.00"));
        when(cuentaRepositoryPort.buscarPorIdParaActualizar(1L)).thenReturn(Optional.of(cuenta));

        assertThatThrownBy(() -> movimientoService.registrar(1L, TipoMovimiento.RETIRO, new BigDecimal("500.00")))
                .isInstanceOf(SaldoNoDisponibleException.class)
                .hasMessage("Saldo no disponible");

        verify(cuentaRepositoryPort, never()).guardar(any());
        verify(movimientoRepositoryPort, never()).guardar(any());
    }

    @Test
    void debeRegistrarRetiroYActualizarSaldoCuandoHaySaldoSuficiente() {
        Cuenta cuenta = cuentaConSaldo(new BigDecimal("500.00"));
        when(cuentaRepositoryPort.buscarPorIdParaActualizar(1L)).thenReturn(Optional.of(cuenta));
        when(cuentaRepositoryPort.guardar(any(Cuenta.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
        when(movimientoRepositoryPort.guardar(any(Movimiento.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Movimiento movimiento = movimientoService.registrar(1L, TipoMovimiento.RETIRO, new BigDecimal("200.00"));

        ArgumentCaptor<Cuenta> cuentaCaptor = ArgumentCaptor.forClass(Cuenta.class);
        verify(cuentaRepositoryPort).guardar(cuentaCaptor.capture());

        assertThat(cuentaCaptor.getValue().getSaldoDisponible()).isEqualByComparingTo("300.00");
        assertThat(movimiento.getValor()).isEqualByComparingTo("-200.00");
        assertThat(movimiento.getSaldo()).isEqualByComparingTo("300.00");
    }

    @Test
    void debeRegistrarDepositoYActualizarSaldoCorrectamente() {
        Cuenta cuenta = cuentaConSaldo(new BigDecimal("500.00"));
        when(cuentaRepositoryPort.buscarPorIdParaActualizar(1L)).thenReturn(Optional.of(cuenta));
        when(cuentaRepositoryPort.guardar(any(Cuenta.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
        when(movimientoRepositoryPort.guardar(any(Movimiento.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Movimiento movimiento = movimientoService.registrar(1L, TipoMovimiento.DEPOSITO, new BigDecimal("150.00"));

        ArgumentCaptor<Cuenta> cuentaCaptor = ArgumentCaptor.forClass(Cuenta.class);
        verify(cuentaRepositoryPort).guardar(cuentaCaptor.capture());

        assertThat(cuentaCaptor.getValue().getSaldoDisponible()).isEqualByComparingTo("650.00");
        assertThat(movimiento.getValor()).isEqualByComparingTo("150.00");
        assertThat(movimiento.getSaldo()).isEqualByComparingTo("650.00");
    }
}
