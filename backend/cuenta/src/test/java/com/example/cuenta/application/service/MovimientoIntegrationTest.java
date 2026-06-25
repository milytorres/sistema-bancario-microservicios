package com.example.cuenta.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.cuenta.domain.exception.SaldoNoDisponibleException;
import com.example.cuenta.domain.model.Cuenta;
import com.example.cuenta.domain.model.Movimiento;
import com.example.cuenta.domain.model.TipoCuenta;
import com.example.cuenta.domain.model.TipoMovimiento;
import com.example.cuenta.domain.port.in.GestionarCuentaUseCase;
import com.example.cuenta.domain.port.in.RegistrarMovimientoUseCase;
import com.example.cuenta.domain.port.out.CuentaRepositoryPort;
import com.example.cuenta.domain.port.out.MovimientoRepositoryPort;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

/**
 * Prueba de integración F6 (PDF): registro de movimiento que valida saldo disponible
 * y persistencia real en base de datos.
 *
 * Carga el contexto completo de Spring (@SpringBootTest) usando los adapters JPA reales
 * (CuentaRepositoryPort / MovimientoRepositoryPort) contra H2 en memoria, sin mocks de
 * Mockito en los beans de negocio. Se diferencia de MovimientoServiceTest (F5, unitario
 * con Mockito) en que aquí se verifica la persistencia real releyendo desde el repositorio.
 *
 * @Transactional + @Rollback (valor por defecto en tests Spring) asegura que cada test
 * sea independiente del orden de ejecución, revirtiendo los cambios en la BD al finalizar.
 */
@SpringBootTest
@Transactional
@Rollback
class MovimientoIntegrationTest {

    @Autowired
    private GestionarCuentaUseCase gestionarCuentaUseCase;

    @Autowired
    private RegistrarMovimientoUseCase registrarMovimientoUseCase;

    @Autowired
    private CuentaRepositoryPort cuentaRepositoryPort;

    @Autowired
    private MovimientoRepositoryPort movimientoRepositoryPort;

    private Cuenta crearCuentaConSaldo(String numeroCuenta, BigDecimal saldoInicial) {
        Cuenta cuenta = Cuenta.builder()
                .numeroCuenta(numeroCuenta)
                .tipoCuenta(TipoCuenta.AHORRO)
                .saldoInicial(saldoInicial)
                .estado(true)
                .clienteId("CLI-INTEGRACION")
                .build();
        return gestionarCuentaUseCase.crear(cuenta);
    }

    @Test
    void debePersistirSaldoActualizadoTrasDeposito() {
        Cuenta cuentaCreada = crearCuentaConSaldo("INT-001", new BigDecimal("1000.00"));

        Movimiento movimiento = registrarMovimientoUseCase.registrar(
                cuentaCreada.getId(), TipoMovimiento.DEPOSITO, new BigDecimal("250.00"));

        assertThat(movimiento.getValor()).isEqualByComparingTo("250.00");
        assertThat(movimiento.getSaldo()).isEqualByComparingTo("1250.00");

        // Verifica persistencia real releyendo desde el repositorio (no el objeto en memoria devuelto).
        Cuenta cuentaPersistida = cuentaRepositoryPort.buscarPorId(cuentaCreada.getId())
                .orElseThrow(() -> new AssertionError("Cuenta no encontrada en BD tras el depósito"));

        assertThat(cuentaPersistida.getSaldoDisponible()).isEqualByComparingTo("1250.00");
    }

    @Test
    void debeRechazarRetiroSiSaldoInsuficienteYNoModificarSaldoEnBD() {
        Cuenta cuentaCreada = crearCuentaConSaldo("INT-002", new BigDecimal("100.00"));

        assertThatThrownBy(() -> registrarMovimientoUseCase.registrar(
                cuentaCreada.getId(), TipoMovimiento.RETIRO, new BigDecimal("500.00")))
                .isInstanceOf(SaldoNoDisponibleException.class)
                .hasMessage("Saldo no disponible");

        // El saldo en BD debe permanecer intacto: el intento fallido no debe tener efectos secundarios.
        Cuenta cuentaPersistida = cuentaRepositoryPort.buscarPorId(cuentaCreada.getId())
                .orElseThrow(() -> new AssertionError("Cuenta no encontrada en BD"));

        assertThat(cuentaPersistida.getSaldoDisponible()).isEqualByComparingTo("100.00");

        List<Movimiento> movimientos = movimientoRepositoryPort.buscarPorCuentaId(cuentaCreada.getId());
        assertThat(movimientos).isEmpty();
    }

    @Test
    void debeRegistrarMovimientoEnHistoricoTrasDepositoExitoso() {
        Cuenta cuentaCreada = crearCuentaConSaldo("INT-003", new BigDecimal("500.00"));

        registrarMovimientoUseCase.registrar(cuentaCreada.getId(), TipoMovimiento.DEPOSITO, new BigDecimal("75.00"));

        List<Movimiento> historico = movimientoRepositoryPort.buscarPorCuentaId(cuentaCreada.getId());

        assertThat(historico).hasSize(1);
        assertThat(historico.get(0).getTipoMovimiento()).isEqualTo(TipoMovimiento.DEPOSITO);
        assertThat(historico.get(0).getValor()).isEqualByComparingTo("75.00");
        assertThat(historico.get(0).getSaldo()).isEqualByComparingTo("575.00");
        assertThat(historico.get(0).getCuentaId()).isEqualTo(cuentaCreada.getId());
    }

    @Test
    void debePermitirRetiroExactoAlSaldoDisponibleYPersistirSaldoCero() {
        Cuenta cuentaCreada = crearCuentaConSaldo("INT-004", new BigDecimal("300.00"));

        Movimiento movimiento = registrarMovimientoUseCase.registrar(
                cuentaCreada.getId(), TipoMovimiento.RETIRO, new BigDecimal("300.00"));

        assertThat(movimiento.getValor()).isEqualByComparingTo("-300.00");
        assertThat(movimiento.getSaldo()).isEqualByComparingTo("0.00");

        Cuenta cuentaPersistida = cuentaRepositoryPort.buscarPorId(cuentaCreada.getId())
                .orElseThrow(() -> new AssertionError("Cuenta no encontrada en BD tras el retiro"));

        assertThat(cuentaPersistida.getSaldoDisponible()).isEqualByComparingTo("0.00");

        Optional<Cuenta> verificacionAdicional = cuentaRepositoryPort.buscarPorNumeroCuenta("INT-004");
        assertThat(verificacionAdicional).isPresent();
        assertThat(verificacionAdicional.get().getSaldoDisponible()).isEqualByComparingTo("0.00");
    }
}
