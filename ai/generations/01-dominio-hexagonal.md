# Fragmento: modelo de dominio puro (arquitectura hexagonal)

Generado por `backend-db-modeler`. Fragmento real de `backend/cuenta/src/main/java/com/example/cuenta/domain/model/Cuenta.java` — nótese la ausencia total de anotaciones JPA/Spring, y el método de negocio embebido en el propio dominio:

```java
@Getter
@Builder(toBuilder = true)
public class Cuenta {

    private Long id;
    private String numeroCuenta;
    private TipoCuenta tipoCuenta;
    private BigDecimal saldoInicial;
    private BigDecimal saldoDisponible;
    private Boolean estado;
    private String clienteId;

    public boolean tieneSaldoSuficiente(BigDecimal monto) {
        return saldoDisponible.compareTo(monto) >= 0;
    }

    public Cuenta aplicarMovimiento(BigDecimal valor) {
        return this.toBuilder()
                .saldoDisponible(this.saldoDisponible.add(valor))
                .build();
    }
}
```

La entidad JPA equivalente (`infrastructure/adapter/out/persistence/entity/CuentaEntity.java`) vive en una capa completamente separada, con las anotaciones de persistencia, y se mapea al dominio vía `CuentaPersistenceMapper` — el dominio nunca conoce JPA.
