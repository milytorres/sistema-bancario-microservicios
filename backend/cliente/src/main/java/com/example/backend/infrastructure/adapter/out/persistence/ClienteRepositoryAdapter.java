package com.example.backend.infrastructure.adapter.out.persistence;

import com.example.backend.domain.model.Cliente;
import com.example.backend.domain.port.out.ClienteRepositoryPort;
import com.example.backend.infrastructure.adapter.out.persistence.mapper.ClientePersistenceMapper;
import com.example.backend.infrastructure.adapter.out.persistence.repository.ClienteJpaRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClienteRepositoryAdapter implements ClienteRepositoryPort {

    private final ClienteJpaRepository jpaRepository;
    private final ClientePersistenceMapper mapper;

    @Override
    public Cliente guardar(Cliente cliente) {
        var entityGuardada = jpaRepository.save(mapper.aEntity(cliente));
        return mapper.aDominio(entityGuardada);
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::aDominio);
    }

    @Override
    public Optional<Cliente> buscarPorClienteId(String clienteId) {
        return jpaRepository.findByClienteId(clienteId).map(mapper::aDominio);
    }

    @Override
    public List<Cliente> buscarTodos() {
        return jpaRepository.findAll().stream().map(mapper::aDominio).toList();
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existePorClienteId(String clienteId) {
        return jpaRepository.existsByClienteId(clienteId);
    }
}
