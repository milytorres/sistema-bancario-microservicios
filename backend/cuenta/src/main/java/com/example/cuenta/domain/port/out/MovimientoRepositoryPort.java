package com.example.cuenta.domain.port.out;

import com.example.cuenta.domain.model.Movimiento;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovimientoRepositoryPort {

    Movimiento guardar(Movimiento movimiento);

    Optional<Movimiento> buscarPorId(Long id);

    List<Movimiento> buscarPorCuentaId(Long cuentaId);

    List<Movimiento> buscarPorCuentaIdsYRangoFechas(List<Long> cuentaIds, LocalDateTime desde, LocalDateTime hasta);

    void eliminar(Long id);
}
