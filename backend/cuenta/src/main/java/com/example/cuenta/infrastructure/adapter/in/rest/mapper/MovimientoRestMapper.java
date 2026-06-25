package com.example.cuenta.infrastructure.adapter.in.rest.mapper;

import com.example.cuenta.domain.model.Movimiento;
import com.example.cuenta.infrastructure.adapter.in.rest.dto.MovimientoResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class MovimientoRestMapper {

    public MovimientoResponseDTO aResponseDTO(Movimiento movimiento) {
        return MovimientoResponseDTO.builder()
                .id(movimiento.getId())
                .fecha(movimiento.getFecha())
                .tipoMovimiento(movimiento.getTipoMovimiento())
                .valor(movimiento.getValor())
                .saldo(movimiento.getSaldo())
                .cuentaId(movimiento.getCuentaId())
                .build();
    }
}
