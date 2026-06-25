package com.example.cuenta.infrastructure.adapter.in.rest;

import com.example.cuenta.domain.port.in.GenerarReporteUseCase;
import com.example.cuenta.infrastructure.adapter.in.rest.dto.ReporteMovimientoResponseDTO;
import com.example.cuenta.infrastructure.adapter.in.rest.mapper.ReporteRestMapper;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final GenerarReporteUseCase reporteUseCase;
    private final ReporteRestMapper mapper;

    /**
     * Contrato del PDF: /reportes?fecha={rango}&cliente={id}.
     * "fecha" se recibe como rango "yyyy-MM-dd,yyyy-MM-dd" (desde,hasta);
     * "cliente" es el clienteId de negocio (ej. CLI-001), no el id interno.
     */
    @GetMapping
    public List<ReporteMovimientoResponseDTO> generar(@RequestParam String fecha, @RequestParam String cliente) {
        LocalDate[] rango = mapper.parsearRangoFechas(fecha);

        return reporteUseCase.generar(cliente, rango[0], rango[1]).stream()
                .map(mapper::aResponseDTO)
                .toList();
    }
}
