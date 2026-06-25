package com.example.cuenta.infrastructure.adapter.out.persistence.repository;

import com.example.cuenta.infrastructure.adapter.out.persistence.entity.MovimientoEntity;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MovimientoJpaRepository extends JpaRepository<MovimientoEntity, Long> {

    List<MovimientoEntity> findByCuenta_Id(Long cuentaId);

    @Query("SELECT m FROM MovimientoEntity m " +
           "WHERE m.cuenta.id IN :cuentaIds " +
           "AND m.fecha BETWEEN :desde AND :hasta " +
           "ORDER BY m.fecha")
    List<MovimientoEntity> findByCuentaIdsYRangoFechas(@Param("cuentaIds") List<Long> cuentaIds,
                                                          @Param("desde") LocalDateTime desde,
                                                          @Param("hasta") LocalDateTime hasta);
}
