package com.example.backend.infrastructure.adapter.in.rest;

import com.example.backend.domain.model.Cliente;
import com.example.backend.domain.port.in.GestionarClienteUseCase;
import com.example.backend.infrastructure.adapter.in.rest.dto.ClientePatchDTO;
import com.example.backend.infrastructure.adapter.in.rest.dto.ClienteRequestDTO;
import com.example.backend.infrastructure.adapter.in.rest.dto.ClienteResponseDTO;
import com.example.backend.infrastructure.adapter.in.rest.mapper.ClienteRestMapper;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final GestionarClienteUseCase clienteUseCase;
    private final ClienteRestMapper mapper;

    @GetMapping
    public List<ClienteResponseDTO> listar() {
        return clienteUseCase.listarTodos().stream()
                .map(mapper::aResponseDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ClienteResponseDTO buscarPorId(@PathVariable Long id) {
        return mapper.aResponseDTO(clienteUseCase.buscarPorId(id));
    }

    @GetMapping("/cliente-id/{clienteId}")
    public ClienteResponseDTO buscarPorClienteId(@PathVariable String clienteId) {
        return mapper.aResponseDTO(clienteUseCase.buscarPorClienteId(clienteId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponseDTO crear(@Valid @RequestBody ClienteRequestDTO request) {
        Cliente creado = clienteUseCase.crear(mapper.aDominio(request));
        return mapper.aResponseDTO(creado);
    }

    @PutMapping("/{id}")
    public ClienteResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequestDTO request) {
        Cliente actualizado = clienteUseCase.actualizar(id, mapper.aDominio(request));
        return mapper.aResponseDTO(actualizado);
    }

    @PatchMapping("/{id}")
    public ClienteResponseDTO actualizarParcial(@PathVariable Long id, @Valid @RequestBody ClientePatchDTO request) {
        Cliente actualizado = clienteUseCase.actualizarParcial(id, mapper.aDominio(request));
        return mapper.aResponseDTO(actualizado);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        clienteUseCase.eliminar(id);
    }
}
