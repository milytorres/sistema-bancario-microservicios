package com.example.cuenta.infrastructure.adapter.in.rest;

import com.example.cuenta.domain.model.Movimiento;
import com.example.cuenta.domain.port.in.RegistrarMovimientoUseCase;
import com.example.cuenta.infrastructure.adapter.in.rest.dto.MovimientoRequestDTO;
import com.example.cuenta.infrastructure.adapter.in.rest.dto.MovimientoResponseDTO;
import com.example.cuenta.infrastructure.adapter.in.rest.mapper.MovimientoRestMapper;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final RegistrarMovimientoUseCase movimientoUseCase;
    private final MovimientoRestMapper mapper;

    @GetMapping
    public List<MovimientoResponseDTO> listarPorCuenta(@RequestParam Long cuentaId) {
        return movimientoUseCase.listarPorCuenta(cuentaId).stream()
                .map(mapper::aResponseDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public MovimientoResponseDTO buscarPorId(@PathVariable Long id) {
        return mapper.aResponseDTO(movimientoUseCase.buscarPorId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovimientoResponseDTO registrar(@Valid @RequestBody MovimientoRequestDTO request) {
        Movimiento movimiento = movimientoUseCase.registrar(request.cuentaId(), request.tipoMovimiento(), request.valor());
        return mapper.aResponseDTO(movimiento);
    }
}
