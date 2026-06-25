package com.example.backend.application.service;

import com.example.backend.domain.exception.ClienteNoEncontradoException;
import com.example.backend.domain.exception.ClienteYaExisteException;
import com.example.backend.domain.model.Cliente;
import com.example.backend.domain.port.in.GestionarClienteUseCase;
import com.example.backend.domain.port.out.ClienteRepositoryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteService implements GestionarClienteUseCase {

    private final ClienteRepositoryPort clienteRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Cliente crear(Cliente cliente) {
        if (clienteRepositoryPort.existePorClienteId(cliente.getClienteId())) {
            throw new ClienteYaExisteException(cliente.getClienteId());
        }
        Cliente conContrasenaHasheada = cliente.toBuilder()
                .contrasena(passwordEncoder.encode(cliente.getContrasena()))
                .build();
        return clienteRepositoryPort.guardar(conContrasenaHasheada);
    }

    @Override
    public List<Cliente> listarTodos() {
        return clienteRepositoryPort.buscarTodos();
    }

    @Override
    public Cliente buscarPorId(Long id) {
        return clienteRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ClienteNoEncontradoException(id));
    }

    @Override
    public Cliente buscarPorClienteId(String clienteId) {
        return clienteRepositoryPort.buscarPorClienteId(clienteId)
                .orElseThrow(() -> new ClienteNoEncontradoException("Cliente no encontrado con clienteId: " + clienteId));
    }

    @Override
    @Transactional
    public Cliente actualizar(Long id, Cliente cliente) {
        Cliente existente = buscarPorId(id);

        Cliente actualizado = Cliente.builder()
                .id(existente.getId())
                .nombre(cliente.getNombre())
                .genero(cliente.getGenero())
                .edad(cliente.getEdad())
                .identificacion(cliente.getIdentificacion())
                .direccion(cliente.getDireccion())
                .telefono(cliente.getTelefono())
                .clienteId(existente.getClienteId())
                .contrasena(passwordEncoder.encode(cliente.getContrasena()))
                .estado(cliente.getEstado())
                .build();

        return clienteRepositoryPort.guardar(actualizado);
    }

    @Override
    @Transactional
    public Cliente actualizarParcial(Long id, Cliente cambios) {
        Cliente existente = buscarPorId(id);

        Cliente actualizado = Cliente.builder()
                .id(existente.getId())
                .nombre(cambios.getNombre() != null ? cambios.getNombre() : existente.getNombre())
                .genero(cambios.getGenero() != null ? cambios.getGenero() : existente.getGenero())
                .edad(cambios.getEdad() != null ? cambios.getEdad() : existente.getEdad())
                .identificacion(cambios.getIdentificacion() != null ? cambios.getIdentificacion() : existente.getIdentificacion())
                .direccion(cambios.getDireccion() != null ? cambios.getDireccion() : existente.getDireccion())
                .telefono(cambios.getTelefono() != null ? cambios.getTelefono() : existente.getTelefono())
                .clienteId(existente.getClienteId())
                .contrasena(cambios.getContrasena() != null ? passwordEncoder.encode(cambios.getContrasena()) : existente.getContrasena())
                .estado(cambios.getEstado() != null ? cambios.getEstado() : existente.getEstado())
                .build();

        return clienteRepositoryPort.guardar(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        buscarPorId(id);
        clienteRepositoryPort.eliminar(id);
    }
}
