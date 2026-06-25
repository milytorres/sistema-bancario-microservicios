package com.example.cuenta.application.service;

import com.example.cuenta.domain.exception.CuentaNoEncontradaException;
import com.example.cuenta.domain.exception.CuentaYaExisteException;
import com.example.cuenta.domain.model.Cuenta;
import com.example.cuenta.domain.port.in.GestionarCuentaUseCase;
import com.example.cuenta.domain.port.out.CuentaRepositoryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CuentaService implements GestionarCuentaUseCase {

    private final CuentaRepositoryPort cuentaRepositoryPort;

    @Override
    @Transactional
    public Cuenta crear(Cuenta cuenta) {
        if (cuentaRepositoryPort.existePorNumeroCuenta(cuenta.getNumeroCuenta())) {
            throw new CuentaYaExisteException(cuenta.getNumeroCuenta());
        }
        Cuenta nueva = cuenta.toBuilder()
                .saldoDisponible(cuenta.getSaldoInicial())
                .build();
        return cuentaRepositoryPort.guardar(nueva);
    }

    @Override
    public List<Cuenta> listarTodas() {
        return cuentaRepositoryPort.buscarTodas();
    }

    @Override
    public Cuenta buscarPorId(Long id) {
        return cuentaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new CuentaNoEncontradaException(id));
    }

    @Override
    @Transactional
    public Cuenta actualizar(Long id, Cuenta cuenta) {
        Cuenta existente = buscarPorId(id);

        Cuenta actualizada = existente.toBuilder()
                .tipoCuenta(cuenta.getTipoCuenta())
                .estado(cuenta.getEstado())
                .build();

        return cuentaRepositoryPort.guardar(actualizada);
    }

    @Override
    @Transactional
    public Cuenta actualizarParcial(Long id, Cuenta cambios) {
        Cuenta existente = buscarPorId(id);

        Cuenta actualizada = existente.toBuilder()
                .tipoCuenta(cambios.getTipoCuenta() != null ? cambios.getTipoCuenta() : existente.getTipoCuenta())
                .estado(cambios.getEstado() != null ? cambios.getEstado() : existente.getEstado())
                .build();

        return cuentaRepositoryPort.guardar(actualizada);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        buscarPorId(id);
        cuentaRepositoryPort.eliminar(id);
    }
}
