package com.example.cuenta.application.service;

import com.example.cuenta.domain.model.ClienteInfo;
import com.example.cuenta.domain.model.Cuenta;
import com.example.cuenta.domain.model.Movimiento;
import com.example.cuenta.domain.model.ReporteMovimiento;
import com.example.cuenta.domain.port.in.GenerarReporteUseCase;
import com.example.cuenta.domain.port.out.ClienteClientPort;
import com.example.cuenta.domain.port.out.CuentaRepositoryPort;
import com.example.cuenta.domain.port.out.MovimientoRepositoryPort;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReporteService implements GenerarReporteUseCase {

    private final CuentaRepositoryPort cuentaRepositoryPort;
    private final MovimientoRepositoryPort movimientoRepositoryPort;
    private final ClienteClientPort clienteClientPort;

    @Override
    public List<ReporteMovimiento> generar(String clienteId, LocalDate desde, LocalDate hasta) {
        List<Cuenta> cuentasDelCliente = cuentaRepositoryPort.buscarPorClienteId(clienteId);

        String nombreCliente = clienteClientPort.buscarPorClienteId(clienteId)
                .map(ClienteInfo::nombre)
                .orElse(clienteId);

        Map<Long, Cuenta> cuentasPorId = cuentasDelCliente.stream()
                .collect(java.util.stream.Collectors.toMap(Cuenta::getId, Function.identity()));

        LocalDateTime desdeFechaHora = desde.atStartOfDay();
        LocalDateTime hastaFechaHora = hasta.atTime(23, 59, 59);

        List<Movimiento> movimientos = movimientoRepositoryPort.buscarPorCuentaIdsYRangoFechas(
                cuentasPorId.keySet().stream().toList(), desdeFechaHora, hastaFechaHora);

        return movimientos.stream()
                .map(movimiento -> {
                    Cuenta cuenta = cuentasPorId.get(movimiento.getCuentaId());
                    return ReporteMovimiento.builder()
                            .fecha(movimiento.getFecha())
                            .cliente(nombreCliente)
                            .numeroCuenta(cuenta.getNumeroCuenta())
                            .tipoCuenta(cuenta.getTipoCuenta())
                            .saldoInicial(cuenta.getSaldoInicial())
                            .estado(cuenta.getEstado())
                            .movimiento(movimiento.getValor())
                            .saldoDisponible(movimiento.getSaldo())
                            .build();
                })
                .toList();
    }
}
