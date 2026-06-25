# Sistema Bancario — Microservicios (Cliente/Persona y Cuenta/Movimiento)

Prueba técnica bancaria implementada como dos microservicios independientes en Spring Boot 3.5.15 + Java 21, con arquitectura hexagonal y PostgreSQL. Este README cubre contexto, arquitectura, ejecución local y con Docker, descripción de los servicios, pruebas, documentación de API y uso de IA.

El historial detallado de decisiones, qué construyó cada agente y validaciones manuales realizadas vive en [`backend/docs/progreso.md`](backend/docs/progreso.md); aquí solo se resume.

## Tabla de contenido

- [1. Contexto y decisiones de diseño](#1-contexto-y-decisiones-de-diseño)
- [2. Microservicios y comunicación](#2-microservicios-y-comunicación)
- [3. Ejecución local (sin Docker)](#3-ejecución-local-sin-docker)
- [4. Ejecución con Docker](#4-ejecución-con-docker)
- [5. Variables de entorno](#5-variables-de-entorno)
- [6. Tests y cobertura (JaCoCo)](#6-tests-y-cobertura-jacoco)
- [7. Swagger UI y colecciones Postman](#7-swagger-ui-y-colecciones-postman)
- [8. Prueba de carga con JMeter](#8-prueba-de-carga-con-jmeter)
- [9. Uso de IA](#9-uso-de-ia)
- [10. Consideraciones no funcionales](#10-consideraciones-no-funcionales)

## 1. Contexto y decisiones de diseño

El enunciado (PDF, dominio bancario F1–F7) pide separar el modelo en `Persona/Cliente` y `Cuenta/Movimiento`. Se decidió llevar esa separación hasta el nivel de **dos microservicios Maven independientes**, cada uno con su propia base de datos PostgreSQL (`banco_cliente` y `banco_cuenta`), en vez de un monolito con un solo esquema:

- `backend/cliente` — paquete base `com.example.backend`, puerto `8081`, base de datos `banco_cliente`.
- `backend/cuenta` — paquete base `com.example.cuenta`, puerto `8082`, base de datos `banco_cuenta`.

Decisiones clave:

- **Arquitectura hexagonal en ambos servicios**: `domain/model` y `domain/port` (in/out) son Java puro sin anotaciones de Spring/JPA; `application/service` orquesta los casos de uso; `infrastructure/adapter/in/rest` expone los controllers REST e `infrastructure/adapter/out/persistence` contiene las entidades JPA, repositorios y mappers hacia/desde el dominio.
- **Sin FK de base de datos entre microservicios**: `cuenta.cliente_id` en la tabla `cuenta` es una referencia lógica (`VARCHAR`) al `cliente_id` del microservicio `cliente`, no una FK real — cada base de datos es autónoma. La FK real entre `cuenta` y `movimiento` sí existe porque ambas tablas viven en la misma base de datos (`banco_cuenta`).
- **Comunicación entre microservicios vía HTTP síncrono**: `cuenta` necesita el nombre del cliente para el reporte de estado de cuenta (F4) y lo obtiene llamando a `cliente` por `RestTemplate` (ver `ClienteHttpAdapter` en `backend/cuenta/src/main/java/com/example/cuenta/infrastructure/adapter/out/client/ClienteHttpAdapter.java`), contra el endpoint `GET /clientes/cliente-id/{clienteId}` expuesto por `cliente`. No se usó mensajería asíncrona (justificación en la sección 10).
- **Seguridad de contraseñas**: la columna `contrasena` de `persona` nunca almacena texto plano, ni siquiera en los datos de ejemplo de `db/BaseDatos.sql` — se guarda el hash BCrypt (vía `spring-security-crypto` en `backend/cliente`). `ClienteResponseDTO` nunca expone ese campo.
- **CORS restringido**: ambos servicios leen `frontend.url` (`FRONTEND_URL`) desde `application.properties` para limitar el origen permitido en el navegador. Esto no bloquea acceso directo vía curl/Postman, lo cual es intencional porque el propio enunciado exige poder probar los endpoints directamente con esas herramientas.
- **Manejo centralizado de errores**: cada microservicio tiene su propio `GlobalExceptionHandler` (`backend/cliente/.../infrastructure/config/GlobalExceptionHandler.java` y el equivalente en `cuenta`), incluyendo el caso de bodies JSON malformados (`HttpMessageNotReadableException` → `400` en vez de `500`, bug real encontrado al correr las colecciones Postman con `newman`).
- **Hallazgo de concurrencia real**: una prueba de carga con JMeter sobre `POST /movimientos` (ver sección 8) expuso un *lost update* en `saldoDisponible` cuando múltiples movimientos concurrentes leían y escribían la misma cuenta. Se corrigió bloqueando la fila a nivel de base de datos con `@Lock(LockModeType.PESSIMISTIC_WRITE)` en `CuentaJpaRepository` (`backend/cuenta/src/main/java/com/example/cuenta/infrastructure/adapter/out/persistence/repository/CuentaJpaRepository.java`).

## 2. Microservicios y comunicación

| | `backend/cliente` | `backend/cuenta` |
|---|---|---|
| Paquete base | `com.example.backend` | `com.example.cuenta` |
| Puerto | `8081` | `8082` |
| Base de datos | `banco_cliente` | `banco_cuenta` |
| Entidades | `Persona` → `Cliente` (herencia JPA `SINGLE_TABLE` sobre la tabla `persona`) | `Cuenta`, `Movimiento` (FK real `movimiento.cuenta_id → cuenta.id`) |
| Endpoints REST | `/clientes` (`GET`, `GET /{id}`, `GET /cliente-id/{clienteId}`, `POST`, `PUT /{id}`, `PATCH /{id}`, `DELETE /{id}`) | `/cuentas` (CRUD completo), `/movimientos` (`GET`, `GET /{id}`, `POST`), `/reportes` (`GET`) |
| Funcionalidades del PDF | F1 (CRUD Cliente) | F1 (CRUD Cuenta), F2 (registrar movimiento), F3 (validar saldo disponible), F4 (reporte de estado de cuenta) |

**Comunicación**: `cuenta` (8082) llama síncronamente por HTTP a `cliente` (8081) en `GET /clientes/cliente-id/{clienteId}` para resolver el nombre del cliente al construir el reporte de `/reportes` (F4). La URL base de ese cliente HTTP es configurable vía `cliente-service.base-url` (`CLIENTE_SERVICE_URL`), y en Docker apunta al nombre del servicio (`http://cliente:8081`) en lugar de `localhost`.

## 3. Ejecución local (sin Docker)

Requisitos: Java 21, PostgreSQL 16 corriendo en `localhost:5432`, Maven Wrapper incluido en cada proyecto (`mvnw` / `mvnw.cmd`).

### 3.1 Levantar PostgreSQL y cargar el esquema

Crear la base de datos `banco_cliente` (usuario `postgres`, password `postgres` por defecto) y ejecutar el script `db/BaseDatos.sql`, que crea las tablas de `persona` en `banco_cliente`, luego crea `banco_cuenta` (`CREATE DATABASE banco_cuenta`) y dentro de ella las tablas `cuenta` y `movimiento`, junto con los datos de ejemplo del PDF.

```bash
psql -U postgres -h localhost -c "CREATE DATABASE banco_cliente;"
psql -U postgres -h localhost -d banco_cliente -f db/BaseDatos.sql
```

(El script ya contiene el `CREATE DATABASE banco_cuenta;` y el `\c banco_cuenta` necesarios para poblar la segunda base desde el mismo archivo.)

### 3.2 Correr `cliente` (puerto 8081)

```bash
cd backend/cliente
./mvnw spring-boot:run
```

Variables relevantes (todas con default apuntando a `localhost`, ver sección 5): si Postgres no usa usuario/password `postgres/postgres`, exportar `DB_USER` y `DB_PASSWORD` antes de levantar.

### 3.3 Correr `cuenta` (puerto 8082)

```bash
cd backend/cuenta
./mvnw spring-boot:run
```

Para que `cuenta` resuelva el nombre del cliente en los reportes, `cliente` debe estar corriendo en `http://localhost:8081` (default de `CLIENTE_SERVICE_URL` cuando no se exporta).

En Windows (PowerShell), usar `.\mvnw.cmd spring-boot:run` en lugar de `./mvnw`.

## 4. Ejecución con Docker

Desde la raíz del proyecto (`D:\Stalin\TRABAJO\PRUEBAS`):

```bash
cp .env.example .env   # ajustar valores si aplica; si no existe .env, docker-compose.yml usa los defaults de .env.example
docker compose up --build -d
```

Esto levanta 3 contenedores definidos en `docker-compose.yml`:

- `banco-postgres` — Postgres 16, con `db/BaseDatos.sql` montado en `/docker-entrypoint-initdb.d/01-BaseDatos.sql` (se ejecuta solo en la primera inicialización del volumen `postgres_data`). Expone `5432`.
- `banco-cliente` — build de `backend/cliente/Dockerfile` (multi-stage: `eclipse-temurin:21-jdk` para compilar con el wrapper, `eclipse-temurin:21-jre-alpine` para runtime). Expone `8081`. Depende de que `db` esté `healthy`.
- `banco-cuenta` — build de `backend/cuenta/Dockerfile`, mismo patrón multi-stage. Expone `8082`. Depende de `db` saludable y de que `cliente` haya iniciado (`CLIENTE_SERVICE_URL=http://cliente:8081`, resolución por nombre de servicio en la red de Docker Compose).

Verificar estado:

```bash
docker compose ps
docker compose logs -f cuenta
```

Apagar:

```bash
docker compose down
```

Para reconstruir desde cero, incluyendo reseteo de los datos de ejemplo (el script de init solo corre si el volumen está vacío):

```bash
docker compose down -v
docker compose up --build -d
```

## 5. Variables de entorno

Definidas con valores por defecto en cada `application.properties` (formato `${VAR:default}`), de modo que ambos servicios corren sin configuración extra contra un Postgres local estándar.

### `backend/cliente/src/main/resources/application.properties`

| Variable | Default | Propósito |
|---|---|---|
| `SERVER_PORT` | `8081` | Puerto HTTP del servicio. |
| `DB_HOST` | `localhost` | Host de PostgreSQL. |
| `DB_PORT` | `5432` | Puerto de PostgreSQL. |
| `DB_NAME` | `banco_cliente` | Base de datos a usar. |
| `DB_USER` | `postgres` | Usuario de PostgreSQL. |
| `DB_PASSWORD` | `postgres` | Password de PostgreSQL. |
| `JPA_DDL_AUTO` | `validate` | Estrategia de Hibernate (`validate`: nunca migra el esquema en runtime, el esquema lo crea `db/BaseDatos.sql`). |
| `JPA_SHOW_SQL` | `false` | Logueo de SQL generado por Hibernate. |
| `FRONTEND_URL` | `http://localhost:4200` | Origen permitido por CORS. |

### `backend/cuenta/src/main/resources/application.properties`

Mismas variables `SERVER_PORT` (default `8082`), `DB_HOST`, `DB_PORT`, `DB_NAME` (default `banco_cuenta`), `DB_USER`, `DB_PASSWORD`, `JPA_DDL_AUTO`, `JPA_SHOW_SQL`, `FRONTEND_URL`, más:

| Variable | Default | Propósito |
|---|---|---|
| `CLIENTE_SERVICE_URL` | `http://localhost:8081` | URL base usada por `RestTemplate` para llamar al microservicio `cliente` desde `ClienteHttpAdapter`. En Docker se sobreescribe a `http://cliente:8081`. |

### `.env.example` (raíz, usado por `docker-compose.yml`)

`DB_USER`, `DB_PASSWORD`, `DB_NAME_CLIENTE`, `DB_NAME_CUENTA`, `FRONTEND_URL` — copiar a `.env` (ignorado por git, ver `.gitignore`) antes de `docker compose up`.

## 6. Tests y cobertura (JaCoCo)

Cada microservicio tiene su suite de pruebas (unitarias de dominio/servicio y de controllers/mappers) y JaCoCo configurado en su `pom.xml` con un umbral mínimo de cobertura de línea del `40%` (`haltOnFailure=false`, es decir, informativo, no bloquea el build si no se alcanza).

```bash
cd backend/cliente
./mvnw test

cd backend/cuenta
./mvnw test
```

El reporte HTML queda en:

- `backend/cliente/target/site/jacoco/index.html`
- `backend/cuenta/target/site/jacoco/index.html`

Cobertura de línea observada en la última corrida: ~49% en `cliente`, ~63% en `cuenta` (ambas por encima del umbral del 40% configurado).

## 7. Swagger UI y colecciones Postman

La documentación OpenAPI se genera en código vía `springdoc-openapi-starter-webmvc-ui` 2.8.9 (`OpenApiConfig` en `infrastructure/config` de cada microservicio) — no hay archivos `.yaml`/`.json` exportados a mano, ambos sirven dinámicamente al levantar el proyecto:

- Cliente: `http://localhost:8081/swagger-ui/index.html` (contrato JSON en `http://localhost:8081/v3/api-docs`).
- Cuenta: `http://localhost:8082/swagger-ui/index.html` (contrato JSON en `http://localhost:8082/v3/api-docs`).

Las colecciones Postman se generaron a partir de ese contrato OpenAPI en ejecución y se guardan dentro de cada proyecto:

- `backend/cliente/src/main/resources/postman/cliente.postman_collection.json`
- `backend/cuenta/src/main/resources/postman/cuenta.postman_collection.json`

Para importarlas: Postman → Import → File → seleccionar el `.json` correspondiente. También se pueden ejecutar headless contra los servicios corriendo con `newman` (usado durante el desarrollo para detectar bugs reales, ver sección 1):

```bash
npx newman run backend/cliente/src/main/resources/postman/cliente.postman_collection.json
npx newman run backend/cuenta/src/main/resources/postman/cuenta.postman_collection.json
```

## 8. Prueba de carga con JMeter

Ubicación: `backend/cuenta/src/test/jmeter/movimientos-carga.jmx`.

Mide el endpoint crítico `POST /movimientos` contra `cuenta` corriendo en `localhost:8082`: 20 hilos concurrentes, 50 iteraciones cada uno (1000 requests), todas `DEPOSITO` sobre cuentas con saldo alto (id `1` y `5`) para evitar contaminar las métricas con fallos de negocio (saldo insuficiente), con una aserción de respuesta `201`. Esta prueba fue la que expuso el bug de concurrencia descrito en la sección 1.

JMeter no viene preinstalado en este entorno; se descargó Apache JMeter 5.6.3 para ejecutar la prueba. Para reproducirla (requiere `cuenta` y su base de datos levantados):

```bash
# Si no tienes JMeter, descárgalo de https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-5.6.3.zip y extráelo
cd backend/cuenta/src/test/jmeter
<ruta-a-jmeter>/bin/jmeter.sh -n -t movimientos-carga.jmx -l resultados.jtl -e -o reporte-html
```

El reporte HTML queda en `backend/cuenta/src/test/jmeter/reporte-html/index.html`, y los resultados crudos en `resultados.jtl` (ambos ya versionados en el repo como evidencia de la última corrida real). También se puede abrir el `.jmx` en la GUI de JMeter para correrlo de forma interactiva.

## 9. Uso de IA

**Pendiente.** La carpeta `ai/` (prompts, resumen de respuestas, fragmentos de código generados por IA, correcciones aplicadas y descripción del agente/asistente usado) todavía no existe en este proyecto. Esta sección se completará referenciando ese contenido en cuanto la carpeta se construya; no se documenta aquí contenido inventado.

Lo que sí existe hoy y es verificable es el uso de agentes especializados de Claude Code (definidos en `.claude/agents/*.md`: `backend-db-modeler`, `backend-task-optimizer`, `backend-api-docs`, `backend-security-review`, `backend-test-generator`, `backend-code-quality`, `backend-performance-test`, `backend-deployment`, `backend-readme-writer`) durante la construcción del backend, con el detalle de qué hizo cada uno en [`backend/docs/progreso.md`](backend/docs/progreso.md). Eso es documentación de proceso interno, no sustituye el entregable formal de la carpeta `ai/` que exige el PDF.

## 10. Consideraciones no funcionales

Lo siguiente es honesto respecto a qué está implementado y qué queda como trabajo futuro razonado, sin necesidad de estar todo construido:

**Implementado:**
- **Concurrencia de escritura**: `@Lock(LockModeType.PESSIMISTIC_WRITE)` en `CuentaJpaRepository` evita el *lost update* de `saldoDisponible` bajo carga concurrente sobre la misma cuenta (validado con la prueba JMeter de la sección 8).
- **Resiliencia de arranque en Docker**: `healthcheck` de Postgres (`pg_isready`) y `depends_on: condition: service_healthy` en `docker-compose.yml` evitan que `cliente`/`cuenta` arranquen antes de que la base de datos esté lista.
- **Manejo centralizado y consistente de errores** (`GlobalExceptionHandler` en ambos servicios), incluyendo bodies malformados y reglas de negocio (saldo no disponible → `422`).
- **Separación de bases de datos** por microservicio, lo que limita el alcance de un fallo o bloqueo de base de datos a un solo dominio.

**Trabajo futuro (no implementado, razonado pero fuera de alcance de esta entrega):**
- **Comunicación asíncrona** entre `cuenta` y `cliente` (p. ej. eventos/mensajería) en lugar de la llamada HTTP síncrona actual vía `RestTemplate`, que acopla la disponibilidad de `cuenta` a la de `cliente` para generar reportes.
- **Cache** de la información de cliente consultada desde `cuenta` para reducir llamadas HTTP repetidas en reportes con muchos movimientos del mismo cliente.
- **Rate limiting / circuit breaker** en el cliente HTTP saliente de `cuenta` (hoy no hay timeout configurado explícitamente ni fallback más allá de capturar `HttpClientErrorException.NotFound`).
- **Escalado horizontal**: no se probó correr múltiples instancias de `cuenta` detrás de un balanceador; el `PESSIMISTIC_WRITE` mitiga condiciones de carrera a nivel de fila de base de datos, pero no se midió su comportamiento con más de un nodo de aplicación.

**Ya completado** (no es trabajo futuro, se incluye aquí para evitar ambigüedad): se hizo una pasada formal completa de `backend-security-review` y `backend-code-quality` sobre todo el código de ambos microservicios, con 10 hallazgos corregidos (hashing de contraseñas, validación de rango de fechas en `/reportes`, `@Valid` en PATCH, excepciones mal nombradas, logging de errores, credenciales movidas a `.env`, CORS, límites de `@Digits` en montos, verificación de que `devtools` no viaja al jar de producción, y la decisión documentada de mantener expuesto el puerto `8081` por requisito de pruebas con Postman).
