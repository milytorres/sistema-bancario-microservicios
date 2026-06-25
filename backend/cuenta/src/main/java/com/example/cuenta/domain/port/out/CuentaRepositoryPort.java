package com.example.cuenta.domain.port.out;

import com.example.cuenta.domain.model.Cuenta;
import java.util.List;
import java.util.Optional;

public interface CuentaRepositoryPort {

    Cuenta guardar(Cuenta cuenta);

    Optional<Cuenta> buscarPorId(Long id);

    /**
     * Igual que buscarPorId, pero con bloqueo pesimista de fila (FOR UPDATE) para
     * operaciones que leen-y-modifican el saldo de forma segura bajo concurrencia.
     */
    Optional<Cuenta> buscarPorIdParaActualizar(Long id);

    Optional<Cuenta> buscarPorNumeroCuenta(String numeroCuenta);

    List<Cuenta> buscarTodas();

    List<Cuenta> buscarPorClienteId(String clienteId);

    void eliminar(Long id);

    boolean existePorNumeroCuenta(String numeroCuenta);
}
