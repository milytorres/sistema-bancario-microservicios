package com.example.cuenta.infrastructure.adapter.in.rest.mapper;

import com.example.cuenta.domain.model.Cuenta;
import com.example.cuenta.infrastructure.adapter.in.rest.dto.CuentaPatchDTO;
import com.example.cuenta.infrastructure.adapter.in.rest.dto.CuentaRequestDTO;
import com.example.cuenta.infrastructure.adapter.in.rest.dto.CuentaResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class CuentaRestMapper {

    public Cuenta aDominio(CuentaRequestDTO dto) {
        return Cuenta.builder()
                .numeroCuenta(dto.numeroCuenta())
                .tipoCuenta(dto.tipoCuenta())
                .saldoInicial(dto.saldoInicial())
                .estado(dto.estado())
                .clienteId(dto.clienteId())
                .build();
    }

    public Cuenta aDominio(CuentaPatchDTO dto) {
        return Cuenta.builder()
                .tipoCuenta(dto.tipoCuenta())
                .estado(dto.estado())
                .build();
    }

    public CuentaResponseDTO aResponseDTO(Cuenta cuenta) {
        return CuentaResponseDTO.builder()
                .id(cuenta.getId())
                .numeroCuenta(cuenta.getNumeroCuenta())
                .tipoCuenta(cuenta.getTipoCuenta())
                .saldoInicial(cuenta.getSaldoInicial())
                .saldoDisponible(cuenta.getSaldoDisponible())
                .estado(cuenta.getEstado())
                .clienteId(cuenta.getClienteId())
                .build();
    }
}
