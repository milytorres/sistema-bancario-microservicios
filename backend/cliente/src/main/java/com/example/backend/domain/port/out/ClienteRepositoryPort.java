package com.example.backend.domain.port.out;

import com.example.backend.domain.model.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteRepositoryPort {

    Cliente guardar(Cliente cliente);

    Optional<Cliente> buscarPorId(Long id);

    Optional<Cliente> buscarPorClienteId(String clienteId);

    List<Cliente> buscarTodos();

    void eliminar(Long id);

    boolean existePorClienteId(String clienteId);
}
