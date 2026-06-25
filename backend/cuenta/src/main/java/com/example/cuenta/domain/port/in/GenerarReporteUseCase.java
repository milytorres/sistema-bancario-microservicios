package com.example.cuenta.domain.port.in;

import com.example.cuenta.domain.model.ReporteMovimiento;
import java.time.LocalDate;
import java.util.List;

public interface GenerarReporteUseCase {

    List<ReporteMovimiento> generar(String clienteId, LocalDate desde, LocalDate hasta);
}
