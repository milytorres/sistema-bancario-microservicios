package com.example.cuenta.application.service;

import com.example.cuenta.domain.exception.CuentaNoEncontradaException;
import com.example.cuenta.domain.exception.MovimientoNoEncontradoException;
import com.example.cuenta.domain.exception.SaldoNoDisponibleException;
import com.example.cuenta.domain.model.Cuenta;
import com.example.cuenta.domain.model.Movimiento;
import com.example.cuenta.domain.model.TipoMovimiento;
import com.example.cuenta.domain.port.in.RegistrarMovimientoUseCase;
import com.example.cuenta.domain.port.out.CuentaRepositoryPort;
import com.example.cuenta.domain.port.out.MovimientoRepositoryPort;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MovimientoService implements RegistrarMovimientoUseCase {

    private final MovimientoRepositoryPort movimientoRepositoryPort;
    private final CuentaRepositoryPort cuentaRepositoryPort;

    @Override
    @Transactional
    public Movimiento registrar(Long cuentaId, TipoMovimiento tipoMovimiento, BigDecimal valor) {
        Cuenta cuenta = cuentaRepositoryPort.buscarPorIdParaActualizar(cuentaId)
                .orElseThrow(() -> new CuentaNoEncontradaException(cuentaId));

        if (tipoMovimiento == TipoMovimiento.RETIRO && !cuenta.tieneSaldoSuficiente(valor)) {
            throw new SaldoNoDisponibleException();
        }

        // El retiro se almacena como valor negativo; el depósito como positivo (F2).
        BigDecimal valorAplicado = tipoMovimiento == TipoMovimiento.RETIRO ? valor.negate() : valor;
        Cuenta cuentaActualizada = cuenta.aplicarMovimiento(valorAplicado);
        cuentaRepositoryPort.guardar(cuentaActualizada);

        Movimiento movimiento = Movimiento.builder()
                .fecha(LocalDateTime.now())
                .tipoMovimiento(tipoMovimiento)
                .valor(valorAplicado)
                .saldo(cuentaActualizada.getSaldoDisponible())
                .cuentaId(cuentaId)
                .build();

        return movimientoRepositoryPort.guardar(movimiento);
    }

    @Override
    public List<Movimiento> listarPorCuenta(Long cuentaId) {
        return movimientoRepositoryPort.buscarPorCuentaId(cuentaId);
    }

    @Override
    public Movimiento buscarPorId(Long id) {
        return movimientoRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new MovimientoNoEncontradoException(id));
    }
}
