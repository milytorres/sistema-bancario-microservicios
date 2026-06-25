package com.example.cuenta.infrastructure.adapter.out.persistence;

import com.example.cuenta.domain.model.Cuenta;
import com.example.cuenta.domain.port.out.CuentaRepositoryPort;
import com.example.cuenta.infrastructure.adapter.out.persistence.mapper.CuentaPersistenceMapper;
import com.example.cuenta.infrastructure.adapter.out.persistence.repository.CuentaJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CuentaRepositoryAdapter implements CuentaRepositoryPort {

    private final CuentaJpaRepository jpaRepository;
    private final CuentaPersistenceMapper mapper;

    @Override
    public Cuenta guardar(Cuenta cuenta) {
        var entityGuardada = jpaRepository.save(mapper.aEntity(cuenta));
        return mapper.aDominio(entityGuardada);
    }

    @Override
    public Optional<Cuenta> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::aDominio);
    }

    @Override
    public Optional<Cuenta> buscarPorIdParaActualizar(Long id) {
        return jpaRepository.findByIdParaActualizar(id).map(mapper::aDominio);
    }

    @Override
    public Optional<Cuenta> buscarPorNumeroCuenta(String numeroCuenta) {
        return jpaRepository.findByNumeroCuenta(numeroCuenta).map(mapper::aDominio);
    }

    @Override
    public List<Cuenta> buscarTodas() {
        return jpaRepository.findAll().stream().map(mapper::aDominio).toList();
    }

    @Override
    public List<Cuenta> buscarPorClienteId(String clienteId) {
        return jpaRepository.findByClienteId(clienteId).stream().map(mapper::aDominio).toList();
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existePorNumeroCuenta(String numeroCuenta) {
        return jpaRepository.existsByNumeroCuenta(numeroCuenta);
    }
}
