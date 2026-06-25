package com.example.backend.infrastructure.adapter.out.persistence.mapper;

import com.example.backend.domain.model.Cliente;
import com.example.backend.infrastructure.adapter.out.persistence.entity.ClienteEntity;
import org.springframework.stereotype.Component;

@Component
public class ClientePersistenceMapper {

    public ClienteEntity aEntity(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        return ClienteEntity.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .genero(cliente.getGenero())
                .edad(cliente.getEdad())
                .identificacion(cliente.getIdentificacion())
                .direccion(cliente.getDireccion())
                .telefono(cliente.getTelefono())
                .clienteId(cliente.getClienteId())
                .contrasena(cliente.getContrasena())
                .estado(cliente.getEstado())
                .build();
    }

    public Cliente aDominio(ClienteEntity entity) {
        if (entity == null) {
            return null;
        }
        return Cliente.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .genero(entity.getGenero())
                .edad(entity.getEdad())
                .identificacion(entity.getIdentificacion())
                .direccion(entity.getDireccion())
                .telefono(entity.getTelefono())
                .clienteId(entity.getClienteId())
                .contrasena(entity.getContrasena())
                .estado(entity.getEstado())
                .build();
    }
}
