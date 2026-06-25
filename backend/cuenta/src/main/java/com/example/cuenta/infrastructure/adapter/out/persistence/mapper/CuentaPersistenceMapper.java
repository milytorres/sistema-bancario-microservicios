package com.example.cuenta.infrastructure.adapter.out.persistence.mapper;

import com.example.cuenta.domain.model.Cuenta;
import com.example.cuenta.infrastructure.adapter.out.persistence.entity.CuentaEntity;
import org.springframework.stereotype.Component;

@Component
public class CuentaPersistenceMapper {

    public CuentaEntity aEntity(Cuenta cuenta) {
        if (cuenta == null) {
            return null;
        }
        return CuentaEntity.builder()
                .id(cuenta.getId())
                .numeroCuenta(cuenta.getNumeroCuenta())
                .tipoCuenta(cuenta.getTipoCuenta())
                .saldoInicial(cuenta.getSaldoInicial())
                .saldoDisponible(cuenta.getSaldoDisponible())
                .estado(cuenta.getEstado())
                .clienteId(cuenta.getClienteId())
                .build();
    }

    public Cuenta aDominio(CuentaEntity entity) {
        if (entity == null) {
            return null;
        }
        return Cuenta.builder()
                .id(entity.getId())
                .numeroCuenta(entity.getNumeroCuenta())
                .tipoCuenta(entity.getTipoCuenta())
                .saldoInicial(entity.getSaldoInicial())
                .saldoDisponible(entity.getSaldoDisponible())
                .estado(entity.getEstado())
                .clienteId(entity.getClienteId())
                .build();
    }
}
