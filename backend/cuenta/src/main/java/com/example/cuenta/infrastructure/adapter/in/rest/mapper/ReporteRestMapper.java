package com.example.cuenta.infrastructure.adapter.in.rest.mapper;

import com.example.cuenta.domain.exception.ParametroReporteInvalidoException;
import com.example.cuenta.domain.model.ReporteMovimiento;
import com.example.cuenta.infrastructure.adapter.in.rest.dto.ReporteMovimientoResponseDTO;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import org.springframework.stereotype.Component;

@Component
public class ReporteRestMapper {

    /**
     * Parsea el parámetro "fecha" del contrato del PDF (/reportes?fecha={rango}&cliente={id}),
     * recibido como rango "yyyy-MM-dd,yyyy-MM-dd". Lanza una excepción de negocio (400) en vez
     * de propagar ArrayIndexOutOfBoundsException/DateTimeParseException como error 500 genérico.
     */
    public LocalDate[] parsearRangoFechas(String fecha) {
        String[] partes = fecha.split(",");
        if (partes.length != 2) {
            throw new ParametroReporteInvalidoException(
                    "El parámetro 'fecha' debe tener el formato 'yyyy-MM-dd,yyyy-MM-dd'");
        }
        try {
            LocalDate desde = LocalDate.parse(partes[0].trim());
            LocalDate hasta = LocalDate.parse(partes[1].trim());
            return new LocalDate[] {desde, hasta};
        } catch (DateTimeParseException ex) {
            throw new ParametroReporteInvalidoException(
                    "El parámetro 'fecha' contiene una fecha inválida: " + fecha);
        }
    }

    public ReporteMovimientoResponseDTO aResponseDTO(ReporteMovimiento reporte) {
        return ReporteMovimientoResponseDTO.builder()
                .fecha(reporte.getFecha())
                .cliente(reporte.getCliente())
                .numeroCuenta(reporte.getNumeroCuenta())
                .tipo(reporte.getTipoCuenta().name())
                .saldoInicial(reporte.getSaldoInicial())
                .estado(reporte.getEstado())
                .movimiento(reporte.getMovimiento())
                .saldoDisponible(reporte.getSaldoDisponible())
                .build();
    }
}
