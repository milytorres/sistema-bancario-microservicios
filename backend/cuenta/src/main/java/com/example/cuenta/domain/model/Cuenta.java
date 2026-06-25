package com.example.cuenta.domain.model;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class Cuenta {

    private Long id;
    private String numeroCuenta;
    private TipoCuenta tipoCuenta;
    private BigDecimal saldoInicial;
    private BigDecimal saldoDisponible;
    private Boolean estado;
    private String clienteId;

    public boolean tieneSaldoSuficiente(BigDecimal monto) {
        return saldoDisponible.compareTo(monto) >= 0;
    }

    public Cuenta aplicarMovimiento(BigDecimal valor) {
        return this.toBuilder()
                .saldoDisponible(this.saldoDisponible.add(valor))
                .build();
    }
}
