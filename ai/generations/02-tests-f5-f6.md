# Fragmento: pruebas F5 (unitaria) y F6 (integración)

Generado por `backend-test-generator`. Fragmento real de `backend/cuenta/src/test/java/com/example/cuenta/application/service/MovimientoServiceTest.java` (F5, unitaria con Mockito, sin Spring context):

```java
@Test
void debeLanzarExcepcionSaldoNoDisponibleSiRetiroSuperaElSaldo() {
    Cuenta cuenta = cuentaConSaldo(new BigDecimal("100.00"));
    when(cuentaRepositoryPort.buscarPorIdParaActualizar(1L)).thenReturn(Optional.of(cuenta));

    assertThatThrownBy(() -> movimientoService.registrar(1L, TipoMovimiento.RETIRO, new BigDecimal("500.00")))
            .isInstanceOf(SaldoNoDisponibleException.class)
            .hasMessage("Saldo no disponible");

    verify(cuentaRepositoryPort, never()).guardar(any());
    verify(movimientoRepositoryPort, never()).guardar(any());
}
```

Fragmento real de `MovimientoIntegrationTest.java` (F6, integración real con `@SpringBootTest` + H2, sin mocks de negocio):

```java
@Test
@Transactional
void debeRechazarRetiroSiSaldoInsuficienteYNoModificarSaldoEnBD() {
    Cuenta cuenta = gestionarCuentaUseCase.crear(cuentaDePrueba(new BigDecimal("100.00")));

    assertThatThrownBy(() ->
            registrarMovimientoUseCase.registrar(cuenta.getId(), TipoMovimiento.RETIRO, new BigDecimal("500.00")))
            .isInstanceOf(SaldoNoDisponibleException.class)
            .hasMessage("Saldo no disponible");

    Cuenta cuentaRecargada = gestionarCuentaUseCase.buscarPorId(cuenta.getId());
    assertThat(cuentaRecargada.getSaldoDisponible()).isEqualByComparingTo("100.00");
}
```

Nota la diferencia clave: F5 verifica la regla de negocio de forma aislada (mock); F6 verifica que, además, el estado realmente persistido en base de datos es consistente tras el intento fallido.
