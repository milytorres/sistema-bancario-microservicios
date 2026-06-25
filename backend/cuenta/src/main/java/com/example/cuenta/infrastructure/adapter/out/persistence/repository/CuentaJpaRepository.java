package com.example.cuenta.infrastructure.adapter.out.persistence.repository;

import com.example.cuenta.infrastructure.adapter.out.persistence.entity.CuentaEntity;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface CuentaJpaRepository extends JpaRepository<CuentaEntity, Long> {

    Optional<CuentaEntity> findByNumeroCuenta(String numeroCuenta);

    boolean existsByNumeroCuenta(String numeroCuenta);

    List<CuentaEntity> findByClienteId(String clienteId);

    /**
     * Bloqueo pesimista (SELECT ... FOR UPDATE) para serializar lecturas-modificaciones
     * concurrentes del saldo - sin esto, bajo carga concurrente se pierde el saldo
     * actualizado por otros hilos (lost update), detectado con la prueba JMeter de F2.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CuentaEntity c WHERE c.id = :id")
    Optional<CuentaEntity> findByIdParaActualizar(Long id);
}
