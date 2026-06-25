# Fragmento: reporte F4 con claves JSON exactas del PDF

Generado por `backend-task-optimizer` + `backend-api-docs`. El PDF exige un JSON con claves específicas en español, con mayúsculas y espacios (`"Numero Cuenta"`, `"Saldo Inicial"`, etc.), que no son nombres válidos de campos Java. Fragmento real de `ReporteMovimientoResponseDTO.java`:

```java
public record ReporteMovimientoResponseDTO(

        @JsonProperty("Fecha")
        LocalDateTime fecha,

        @JsonProperty("Cliente")
        String cliente,

        @JsonProperty("Numero Cuenta")
        String numeroCuenta,

        @JsonProperty("Saldo Inicial")
        BigDecimal saldoInicial,

        @JsonProperty("Saldo Disponible")
        BigDecimal saldoDisponible
        // ...
) {}
```

Verificación real contra el ejemplo del PDF (Sección 7.5), ejecutando el endpoint con los datos de `BaseDatos.sql`:

```bash
curl "http://localhost:8082/reportes?fecha=2022-02-01,2022-02-28&cliente=CLI-002"
```

```json
[
  {"Fecha":"2022-02-08T10:00:00","Cliente":"Marianela Montalvo","Numero Cuenta":"496825","Movimiento":-540.00,"Saldo Inicial":540.00,"Saldo Disponible":0.00},
  {"Fecha":"2022-02-10T10:00:00","Cliente":"Marianela Montalvo","Numero Cuenta":"225487","Movimiento":600.00,"Saldo Inicial":100.00,"Saldo Disponible":700.00}
]
```

Coincide exactamente con los 2 registros de ejemplo del PDF.
