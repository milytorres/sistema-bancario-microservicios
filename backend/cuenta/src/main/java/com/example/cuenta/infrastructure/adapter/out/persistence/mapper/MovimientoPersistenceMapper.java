package com.example.cuenta.infrastructure.adapter.out.persistence.mapper;

import com.example.cuenta.domain.model.Movimiento;
import com.example.cuenta.infrastructure.adapter.out.persistence.entity.CuentaEntity;
import com.example.cuenta.infrastructure.adapter.out.persistence.entity.MovimientoEntity;
import org.springframework.stereotype.Component;

@Component
public class MovimientoPersistenceMapper {

    public MovimientoEntity aEntity(Movimiento movimiento) {
        if (movimiento == null) {
            return null;
        }
        CuentaEntity cuentaRef = CuentaEntity.builder().id(movimiento.getCuentaId()).build();

        return MovimientoEntity.builder()
                .id(movimiento.getId())
                .fecha(movimiento.getFecha())
                .tipoMovimiento(movimiento.getTipoMovimiento())
                .valor(movimiento.getValor())
                .saldo(movimiento.getSaldo())
                .cuenta(cuentaRef)
                .build();
    }

    public Movimiento aDominio(MovimientoEntity entity) {
        if (entity == null) {
            return null;
        }
        return Movimiento.builder()
                .id(entity.getId())
                .fecha(entity.getFecha())
                .tipoMovimiento(entity.getTipoMovimiento())
                .valor(entity.getValor())
                .saldo(entity.getSaldo())
                .cuentaId(entity.getCuenta().getId())
                .build();
    }
}
