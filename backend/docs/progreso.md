# Progreso del proyecto — Sistema Bancario (Microservicios)

Registro de lo construido hasta ahora y qué agente (`.claude/agents/`) fue responsable de cada parte. Los agentes se ejecutaron dentro de esta misma sesión de Claude Code, aplicando sus reglas definidas en `.claude/agents/*.md` al código generado.

## Estado general

| Microservicio | Puerto | Base de datos | Estado |
|---|---|---|---|
| `backend/cliente` (Cliente/Persona) | 8081 | `banco_cliente` | F1 completo, probado end-to-end. Tests: 19/19. Cobertura JaCoCo: ~49%. |
| `backend/cuenta` (Cuenta/Movimiento) | 8082 | `banco_cuenta` | F1, F2, F3, F4 completos, probados end-to-end. Tests: 29/29. Cobertura JaCoCo: ~63%. |

F1–F7 completos y verificados con ejecución real (no solo compilación). Pasadas formales de calidad/seguridad completas con hallazgos corregidos.

## Detalle por agente

### `backend-db-modeler`
- Diseñó la arquitectura hexagonal de ambos microservicios (`domain/model`, `domain/port`, `application/service`, `infrastructure/...`).
- Modeló `Persona`/`Cliente` (herencia JPA `SINGLE_TABLE`) en `cliente`.
- Modeló `Cuenta`/`Movimiento` en `cuenta`, sin FK de base de datos hacia `cliente` (cada microservicio tiene su propia base de datos: `banco_cliente` y `banco_cuenta`).
- Configuró la conexión a PostgreSQL en ambos `application.properties` vía variables de entorno.
- Construyó y mantiene `db/BaseDatos.sql` (esquema + datos de ejemplo del PDF para `persona`, `cuenta`, `movimiento`; contraseñas seed con hash BCrypt, no texto plano).
- Configuró `docker-compose.yml` (un contenedor Postgres, 2 bases de datos lógicas).

### `backend-task-optimizer`
- Generó el CRUD completo de `Cliente` (`ClienteController`, `ClienteService`, DTOs, `ClienteRestMapper`) cumpliendo F1.
- Generó el CRUD de `Cuenta` (`CuentaController`, `CuentaService`, DTOs, mapper) y el registro de `Movimiento` (`MovimientoController`, `MovimientoService`) cumpliendo F1 y F2.
- Implementó la lógica de actualización de saldo (depósito positivo / retiro negativo) dentro de `Cuenta.aplicarMovimiento(...)`.

### `backend-test-generator`
- F5: tests unitarios de dominio (`ClienteTest`, `CuentaTest`), de servicio con Mockito (`ClienteServiceTest`, `MovimientoServiceTest`), y de controllers con `@WebMvcTest`/`MockMvc` (`ClienteControllerTest`, `CuentaControllerTest`, `MovimientoControllerTest`, `ReporteControllerTest`) y de mappers REST.
- F6: prueba de integración real con `@SpringBootTest` + H2 (`MovimientoIntegrationTest`) validando saldo y persistencia (sin mocks en el flujo de negocio).
- Configuró JaCoCo en ambos `pom.xml` (umbral 40% en líneas, no bloqueante) y subió la cobertura real agregando tests donde había 0% (controllers, mappers REST): `cliente` 12%→49%, `cuenta` 37%→63%.
- Configuró H2 en memoria (`src/test/resources/application.properties`) para que `mvn test` pase sin depender de PostgreSQL real.

### `backend-api-docs`
- Agregó `springdoc-openapi-starter-webmvc-ui` a ambos `pom.xml` (versión `2.8.9`, tras detectar y corregir un `NoSuchMethodError` por incompatibilidad de la versión `2.6.0` con Spring Framework 6.2.x de Spring Boot 3.5.15).
- Configuró `OpenApiConfig` en ambos microservicios — Swagger queda **integrado en el propio código**, sin archivos exportados: `http://localhost:8081/swagger-ui/index.html` (cliente) y `http://localhost:8082/swagger-ui/index.html` (cuenta).
- Generó las colecciones Postman a partir del contrato OpenAPI real en ejecución, ubicadas dentro de cada proyecto (`backend/<servicio>/src/main/resources/postman/`).
- Ejecutó las colecciones con `newman` contra los servicios reales corriendo, lo que detectó un bug real: un body con tipos mal formados devolvía `500` en vez de `400`. Corregido con `HttpMessageNotReadableException` en ambos `GlobalExceptionHandler`.

### `backend-code-quality` y `backend-security-review` (pasada formal completa)
10 hallazgos detectados y corregidos sobre todo el código de ambos microservicios:
1. **Crítico** — contraseñas en texto plano: agregado `spring-security-crypto` + `BCryptPasswordEncoder`, hash aplicado en `crear`/`actualizar`/`actualizarParcial` de `ClienteService`; `BaseDatos.sql` actualizado con hashes reales.
2. **Crítico** — `ReporteController` devolvía `500` ante fecha malformada: parsing movido a `ReporteRestMapper.parsearRangoFechas()` + `ParametroReporteInvalidoException` → `400`.
3. Validación "muerta" en PATCH: agregado `@Valid` en `ClienteController`/`CuentaController`.
4. `MovimientoNoEncontradoException` propia (ya no reutiliza `CuentaNoEncontradaException`).
5. Logging agregado en el handler genérico de excepciones de ambos servicios.
6. Credenciales movidas de `docker-compose.yml` a variables `${DB_USER}`/`${DB_PASSWORD}` + `.env.example` + `.gitignore`.
7. CORS explícito (`CorsConfig`) restringido a `FRONTEND_URL` en ambos servicios.
8. `@Digits(integer=13, fraction=2)` en montos (`MovimientoRequestDTO.valor`, `CuentaRequestDTO.saldoInicial`), coherente con `NUMERIC(15,2)`.
9. Decisión documentada (no un fix de código): puerto `8081` se mantiene expuesto porque el PDF exige probar `/clientes` con Postman; mitigado parcialmente por CORS.
10. Verificado que `spring-boot-devtools` no queda empaquetado en el jar de producción (inspección directa del jar dentro del contenedor Docker).

### `backend-deployment`
- Creó `Dockerfile` multi-stage para ambos microservicios (`eclipse-temurin:21-jdk` build, `eclipse-temurin:21-jre-alpine` runtime).
- Integró `cliente` y `cuenta` al `docker-compose.yml` de la raíz (junto al servicio `db` ya existente), con `depends_on`/healthchecks y variables de entorno.
- Validó `docker compose up --build -d` end-to-end: los 3 contenedores corren y los endpoints responden `200`, incluyendo la comunicación HTTP interna `cuenta → cliente` dentro de la red Docker.

### `backend-performance-test`
- Diseñó y ejecutó una prueba de carga real con Apache JMeter sobre `POST /movimientos` (endpoint crítico, F2): 20 threads, ramp-up 5s, 50 loops = 1000 requests, `DEPOSITO` sobre cuenta de saldo alto.
- **Encontró un bug de concurrencia real**: lost-update en `saldoDisponible` (58.5% de actualizaciones perdidas bajo carga concurrente, sin relación con JMeter como herramienta — bug real de la lógica de negocio).
- El fix (`@Lock(LockModeType.PESSIMISTIC_WRITE)` en `CuentaJpaRepository`, usado en `MovimientoService.registrar()`) se verificó re-ejecutando la misma carga: saldo final exacto (`11425.00` vs. esperado `11425.00`), sin pérdida.
- Artefactos en `backend/cuenta/src/test/jmeter/`: `movimientos-carga.jmx`, `resultados.jtl`, `reporte-html/`.

### `backend-readme-writer`
- Escribió `README.md` en la raíz del proyecto: contexto y decisiones de arquitectura, ejecución local y con Docker, descripción de microservicios y su comunicación, variables de entorno, tests/cobertura, Swagger/Postman, JMeter, consideraciones no funcionales (implementado vs. trabajo futuro), y sección de IA marcada explícitamente como pendiente (la carpeta `ai/` aún no existe).

## Validaciones manuales/automatizadas realizadas (no solo compilación)

- F1: CRUD de `/clientes`, `/cuentas`, `/movimientos` probado con `curl` y colecciones Postman vía `newman`.
- F2: depósito actualiza `saldoDisponible` correctamente, persistencia confirmada en BD real.
- F3: retiro que excede saldo devuelve `422` con mensaje exacto `"Saldo no disponible"`.
- F4: `/reportes?fecha=2022-02-01,2022-02-28&cliente=CLI-002` devuelve exactamente los 2 registros del ejemplo del PDF (Sección 7.5), con las claves JSON literales (`"Numero Cuenta"`, `"Saldo Inicial"`, `"Saldo Disponible"`).
- Comunicación entre microservicios verificada dentro de la red Docker, no solo en localhost.
- F5/F6: 48 tests automatizados (19 + 29) pasando, incluyendo integración real con persistencia.
- Prueba de carga JMeter real, con bug de concurrencia encontrado y corregido, verificado con una segunda corrida.

## Pendiente

- Frontend Angular.
- Carpeta `ai/` (prompts, generaciones, decisiones, agente) — entregable obligatorio del PDF, distinto de esta carpeta `backend/docs/`.
