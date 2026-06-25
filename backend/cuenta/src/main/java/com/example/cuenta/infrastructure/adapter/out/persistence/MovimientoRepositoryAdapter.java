package com.example.cuenta.infrastructure.adapter.out.persistence;

import com.example.cuenta.domain.model.Movimiento;
import com.example.cuenta.domain.port.out.MovimientoRepositoryPort;
import com.example.cuenta.infrastructure.adapter.out.persistence.mapper.MovimientoPersistenceMapper;
import com.example.cuenta.infrastructure.adapter.out.persistence.repository.MovimientoJpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovimientoRepositoryAdapter implements MovimientoRepositoryPort {

    private final MovimientoJpaRepository jpaRepository;
    private final MovimientoPersistenceMapper mapper;

    @Override
    public Movimiento guardar(Movimiento movimiento) {
        var entityGuardada = jpaRepository.save(mapper.aEntity(movimiento));
        return mapper.aDominio(entityGuardada);
    }

    @Override
    public Optional<Movimiento> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::aDominio);
    }

    @Override
    public List<Movimiento> buscarPorCuentaId(Long cuentaId) {
        return jpaRepository.findByCuenta_Id(cuentaId).stream().map(mapper::aDominio).toList();
    }

    @Override
    public List<Movimiento> buscarPorCuentaIdsYRangoFechas(List<Long> cuentaIds, LocalDateTime desde, LocalDateTime hasta) {
        if (cuentaIds.isEmpty()) {
            return List.of();
        }
        return jpaRepository.findByCuentaIdsYRangoFechas(cuentaIds, desde, hasta).stream()
                .map(mapper::aDominio)
                .toList();
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }
}
