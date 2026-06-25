package com.example.cuenta.domain.port.in;

import com.example.cuenta.domain.model.Movimiento;
import com.example.cuenta.domain.model.TipoMovimiento;
import java.math.BigDecimal;
import java.util.List;

public interface RegistrarMovimientoUseCase {

    Movimiento registrar(Long cuentaId, TipoMovimiento tipoMovimiento, BigDecimal valor);

    List<Movimiento> listarPorCuenta(Long cuentaId);

    Movimiento buscarPorId(Long id);
}
