# Prompts utilizados

Resumen de los prompts reales usados para invocar cada subagente (ver `agent/`), organizados por objetivo. Los prompts completos y verbatim quedan en el historial de la sesión; aquí se resume su contenido esencial — qué se le pidió a cada agente y con qué contexto.

## 1. Scaffolding / modelado (arquitectura hexagonal, dominio, persistencia)

**Agente:** `backend-db-modeler`

- *"Empecemos con la creación de la base, vamos a trabajar dentro de la carpeta 'backend' con una estructura hexagonal."* → generó `domain/model/Persona.java`, `Cliente.java`, los puertos `domain/port/out/ClienteRepositoryPort.java`, las entidades JPA (`PersonaEntity`, `ClienteEntity` con herencia `SINGLE_TABLE`), el mapper de persistencia, el adapter, y `application.properties` con conexión a PostgreSQL vía variables de entorno.
- *"Diseñemos Cuenta/Movimiento"* (repetido tras corregir el alcance de 1 servicio a 2 microservicios separados) → modelo `Cuenta`/`Movimiento`, enums `TipoCuenta`/`TipoMovimiento`, puertos, entidades JPA sin FK cruzada entre microservicios, `db/BaseDatos.sql` con 2 bases de datos (`banco_cliente`, `banco_cuenta`).

**Agente:** `backend-task-optimizer`

- *"Ayúdame creando todo lo que me indicas: caso de uso, servicio, DTOs, mapper REST, controller y manejo de excepciones para Cliente."* → `GestionarClienteUseCase`, `ClienteService`, `ClienteRequestDTO`/`ClientePatchDTO`/`ClienteResponseDTO`, `ClienteRestMapper`, `ClienteController`, `GlobalExceptionHandler`. Repetido análogamente para `Cuenta`/`Movimiento`/`Reporte` en el segundo microservicio.

## 2. Pruebas (F5 unitarias, F6 integración, cobertura)

**Agente:** `backend-test-generator`

- *"Comencemos con F5, las pruebas unitarias respectivas"* → tests de dominio puro (`ClienteTest`, `CuentaTest`) y de servicio con Mockito (`ClienteServiceTest`, `MovimientoServiceTest`), con instrucción explícita de ejecutar `mvn test` realmente y no asumir que pasan.
- *"Ahora F6"* → prueba de integración real (`MovimientoIntegrationTest`) con `@SpringBootTest` + H2, sin mocks en el flujo de negocio, validando saldo y persistencia.
- *"Subamos la cobertura JaCoCo por encima del 40% de forma real"* → tests de `@WebMvcTest` para los 4 controllers y los mappers REST, con instrucción explícita de no relajar el umbral del `pom.xml` para forzar el "pase".

## 3. Documentación de API (Swagger, Postman)

**Agente:** `backend-api-docs`

- *"Documentación de Swagger y Postman, en una carpeta docs, lo que se ha realizado y qué agente lo ha realizado"* → agregó `springdoc-openapi`, configuró `OpenApiConfig`, extrajo el contrato real desde `/v3/api-docs` con los servicios corriendo, generó las colecciones Postman con `openapi-to-postmanv2`, y las validó ejecutándolas con `newman` contra los servicios reales (lo cual detectó un bug real de manejo de excepciones).

## 4. Despliegue (Docker, F7)

**Agente:** `backend-deployment`

- *"Crear los Dockerfile de cada microservicio, integrarlos al docker-compose.yml, preparar variables de entorno para Render"* → `Dockerfile` multi-stage por servicio, integración al `docker-compose.yml` raíz, validación real con `docker compose up --build -d` y pruebas `curl` contra los 3 contenedores.

## 5. Pruebas de carga (JMeter)

**Agente:** `backend-performance-test`

- *"Prueba de carga sobre POST /movimientos: 20 threads, ramp-up 5s, 50 loops, usando DEPOSITO sobre cuentas de saldo alto para no contaminar las métricas con fallos esperados de F3"* → diseñó y ejecutó el plan `.jmx`, generó el reporte HTML, y **detectó un bug de concurrencia real** (lost-update en el saldo) que no estaba en el alcance original del prompt pero que la ejecución real expuso.

## 6. Revisión formal de calidad y seguridad

**Agentes:** `backend-code-quality` y `backend-security-review` (invocados en paralelo, modo solo lectura)

- *"Primera pasada formal de revisión de calidad/seguridad de todo el proyecto antes de la entrega final, reporta hallazgos priorizados con archivo:línea, no corrijas nada tú mismo"* → 2 informes independientes con hallazgos reales (ej. contraseñas en texto plano, bug de parsing de fechas devolviendo 500, validación "muerta" en PATCH). Las correcciones se aplicaron manualmente después (ver `decisions.md`), no por estos agentes (son de solo lectura por diseño).

## 7. README del proyecto

**Agente:** `backend-readme-writer`

- *"Escribe el README cumpliendo la Sección 8.1 del PDF, basado en el código y configuración real, sin inventar nada, y deja la sección de IA como pendiente porque ai/ todavía no existe"* → generó `README.md` verificando cada comando/ruta contra el proyecto real antes de incluirlo, y señaló proactivamente que `backend/docs/progreso.md` estaba desactualizado en dos puntos.
