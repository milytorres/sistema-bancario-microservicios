package com.example.backend.domain.port.in;

import com.example.backend.domain.model.Cliente;
import java.util.List;

public interface GestionarClienteUseCase {

    Cliente crear(Cliente cliente);

    List<Cliente> listarTodos();

    Cliente buscarPorId(Long id);

    Cliente buscarPorClienteId(String clienteId);

    Cliente actualizar(Long id, Cliente cliente);

    Cliente actualizarParcial(Long id, Cliente cambios);

    void eliminar(Long id);
}
