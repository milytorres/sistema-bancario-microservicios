package com.example.cuenta.infrastructure.adapter.in.rest;

import com.example.cuenta.domain.model.Cuenta;
import com.example.cuenta.domain.port.in.GestionarCuentaUseCase;
import com.example.cuenta.infrastructure.adapter.in.rest.dto.CuentaPatchDTO;
import com.example.cuenta.infrastructure.adapter.in.rest.dto.CuentaRequestDTO;
import com.example.cuenta.infrastructure.adapter.in.rest.dto.CuentaResponseDTO;
import com.example.cuenta.infrastructure.adapter.in.rest.mapper.CuentaRestMapper;
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
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final GestionarCuentaUseCase cuentaUseCase;
    private final CuentaRestMapper mapper;

    @GetMapping
    public List<CuentaResponseDTO> listar() {
        return cuentaUseCase.listarTodas().stream()
                .map(mapper::aResponseDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public CuentaResponseDTO buscarPorId(@PathVariable Long id) {
        return mapper.aResponseDTO(cuentaUseCase.buscarPorId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CuentaResponseDTO crear(@Valid @RequestBody CuentaRequestDTO request) {
        Cuenta creada = cuentaUseCase.crear(mapper.aDominio(request));
        return mapper.aResponseDTO(creada);
    }

    @PutMapping("/{id}")
    public CuentaResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody CuentaRequestDTO request) {
        Cuenta actualizada = cuentaUseCase.actualizar(id, mapper.aDominio(request));
        return mapper.aResponseDTO(actualizada);
    }

    @PatchMapping("/{id}")
    public CuentaResponseDTO actualizarParcial(@PathVariable Long id, @Valid @RequestBody CuentaPatchDTO request) {
        Cuenta actualizada = cuentaUseCase.actualizarParcial(id, mapper.aDominio(request));
        return mapper.aResponseDTO(actualizada);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        cuentaUseCase.eliminar(id);
    }
}
