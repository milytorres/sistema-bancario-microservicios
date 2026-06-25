# Fragmento: bug de concurrencia encontrado por JMeter y su fix

Generado por `backend-performance-test` (hallazgo) + corrección manual posterior (ver `../decisions.md` punto 4).

## Antes (código que causaba el lost-update)

`MovimientoService.registrar` leía la cuenta sin bloqueo:

```java
Cuenta cuenta = cuentaRepositoryPort.buscarPorId(cuentaId)
        .orElseThrow(() -> new CuentaNoEncontradaException(cuentaId));
```

Bajo 20 threads concurrentes sobre la misma cuenta, múltiples transacciones leían el mismo `saldoDisponible`, calculaban el nuevo valor en memoria, y se sobrescribían entre sí al guardar — 585 de 1000 actualizaciones se perdieron en la prueba real.

## Después (fix con bloqueo pesimista)

`CuentaJpaRepository.java`:

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT c FROM CuentaEntity c WHERE c.id = :id")
Optional<CuentaEntity> findByIdParaActualizar(Long id);
```

`MovimientoService.registrar` ahora usa esa variante bloqueante dentro de la misma transacción (`@Transactional`):

```java
Cuenta cuenta = cuentaRepositoryPort.buscarPorIdParaActualizar(cuentaId)
        .orElseThrow(() -> new CuentaNoEncontradaException(cuentaId));
```

## Evidencia de la corrección (re-ejecución de la misma carga)

| | Antes del fix | Después del fix |
|---|---|---|
| Saldo esperado | 11425.00 | 11425.00 |
| Saldo real en BD | 5235.00 ❌ | 11425.00 ✅ |
| Movimientos insertados | 1000/1000 (correcto) | 1000/1000 (correcto) |

El insert del movimiento (el registro histórico) nunca se vio afectado — el bug era específicamente en la actualización del saldo acumulado de la cuenta.
