package com.example.cuenta.infrastructure.adapter.out.persistence.entity;

import com.example.cuenta.domain.model.TipoCuenta;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cuenta")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_cuenta", nullable = false, unique = true, length = 20)
    private String numeroCuenta;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cuenta", nullable = false, length = 20)
    private TipoCuenta tipoCuenta;

    @Column(name = "saldo_inicial", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoInicial;

    @Column(name = "saldo_disponible", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoDisponible;

    @Column(nullable = false)
    private Boolean estado;

    @Column(name = "cliente_id", nullable = false, length = 50)
    private String clienteId;
}
