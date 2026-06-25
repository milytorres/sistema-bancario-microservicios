package com.example.backend.infrastructure.adapter.in.rest.mapper;

import com.example.backend.domain.model.Cliente;
import com.example.backend.infrastructure.adapter.in.rest.dto.ClientePatchDTO;
import com.example.backend.infrastructure.adapter.in.rest.dto.ClienteRequestDTO;
import com.example.backend.infrastructure.adapter.in.rest.dto.ClienteResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ClienteRestMapper {

    public Cliente aDominio(ClienteRequestDTO dto) {
        return Cliente.builder()
                .nombre(dto.nombre())
                .genero(dto.genero())
                .edad(dto.edad())
                .identificacion(dto.identificacion())
                .direccion(dto.direccion())
                .telefono(dto.telefono())
                .clienteId(dto.clienteId())
                .contrasena(dto.contrasena())
                .estado(dto.estado())
                .build();
    }

    public Cliente aDominio(ClientePatchDTO dto) {
        return Cliente.builder()
                .nombre(dto.nombre())
                .genero(dto.genero())
                .edad(dto.edad())
                .identificacion(dto.identificacion())
                .direccion(dto.direccion())
                .telefono(dto.telefono())
                .contrasena(dto.contrasena())
                .estado(dto.estado())
                .build();
    }

    public ClienteResponseDTO aResponseDTO(Cliente cliente) {
        return ClienteResponseDTO.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .genero(cliente.getGenero())
                .edad(cliente.getEdad())
                .identificacion(cliente.getIdentificacion())
                .direccion(cliente.getDireccion())
                .telefono(cliente.getTelefono())
                .clienteId(cliente.getClienteId())
                .estado(cliente.getEstado())
                .build();
    }
}
