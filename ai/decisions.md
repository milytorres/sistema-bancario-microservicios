# Decisiones — qué se aceptó, qué se corrigió manualmente y por qué

Este archivo documenta la validación humana del trabajo generado por los 9 agentes (ver `agent/`), tal como exige el criterio de evaluación "criterio humano: el candidato detecta y corrige errores o alucinaciones de la IA" de la Sección 6.3 del PDF. Todas las entradas corresponden a eventos reales de esta sesión de desarrollo, no a ejemplos hipotéticos.

## 1. Corrección de alcance arquitectónico (antes de tocar código)

**Lo que la IA interpretó primero**: ante la instrucción de trabajar "dentro de la carpeta backend con estructura hexagonal", el asistente construyó `Cuenta`/`Movimiento` como parte del **mismo** proyecto Maven que `Cliente`/`Persona` (un solo microservicio).

**Por qué se corrigió**: el usuario aclaró que el PDF exige 2 microservicios separados, y que la intención era 2 proyectos Maven independientes dentro de `backend/` (`backend/cliente/`, `backend/cuenta/`), cada uno con su propia base de datos.

**Acción**: se eliminó todo el código de `Cuenta`/`Movimiento` que había quedado mezclado en el proyecto de `cliente`, se corrigió el `artifactId` del `pom.xml`, y se reconstruyó `Cuenta`/`Movimiento` desde cero dentro de `backend/cuenta/` con su propio paquete (`com.example.cuenta`) y base de datos (`banco_cuenta`), eliminando la FK de base de datos entre microservicios que ya no aplicaba.

## 2. Versión de dependencia incompatible (springdoc-openapi)

**Lo que la IA generó primero**: `springdoc-openapi-starter-webmvc-ui` versión `2.6.0`.

**Error detectado**: al levantar el servicio, `/v3/api-docs` devolvía `500` con `NoSuchMethodError: ControllerAdviceBean.<init>(Object)` — incompatibilidad real entre esa versión de springdoc y el Spring Framework 6.2.x que trae Spring Boot 3.5.15.

**Corrección**: se investigó el issue real en el repositorio de springdoc (no se asumió una solución), confirmando que la versión `2.8.9` corrige el problema. Se actualizó en ambos `pom.xml` y se verificó que `/v3/api-docs` respondiera `200` con el contrato completo antes de continuar.

## 3. Lógica de negocio confusa generada por la IA, simplificada por revisión humana

**Lo que la IA generó primero** (en `MovimientoService.registrar`): una expresión condicional para validar saldo con doble negación y `.max()` anidados, difícil de leer y con alto riesgo de bug oculto.

**Corrección manual inmediata**: se reescribió como una condición simple y legible (`if (RETIRO && !tieneSaldoSuficiente(valor)) throw ...`) antes de seguir construyendo sobre esa base — se priorizó claridad sobre la primera solución generada, sin esperar a que un bug la expusiera.

## 4. Bug de concurrencia real, descubierto por una prueba de carga generada por IA

**Lo que pasó**: el agente `backend-performance-test` diseñó y ejecutó una prueba de carga real (no simulada) de 1000 requests concurrentes sobre `POST /movimientos`. El resultado esperado era medir throughput/latencia; lo que realmente reveló fue un **bug de integridad financiera**: el saldo final (`5235.00`) no coincidía con el esperado matemáticamente (`11425.00`) — un *lost update* clásico por falta de bloqueo en lectura-modificación-escritura concurrente.

**Por qué es relevante como validación humana**: el agente reportó el hallazgo, pero la decisión de **qué tipo de fix aplicar** (bloqueo pesimista `@Lock(PESSIMISTIC_WRITE)` a nivel de fila, en vez de bloqueo optimista con `@Version` + reintentos) fue una decisión de diseño humana, evaluando que para una operación financiera no es deseable que el cliente de la API tenga que reintentar ante un conflicto — es preferible que el servidor serialice el acceso a la fila.

**Verificación**: se reconstruyó la imagen Docker con el fix, se reseteó la base de datos, y se re-ejecutó exactamente la misma carga (1000 requests, 20 threads). Resultado: saldo final exacto, sin pérdida. El fix se confirmó con datos reales, no se asumió correcto solo por compilar.

## 5. Tests rotos por cambios posteriores (efecto en cadena de una corrección de seguridad)

**Lo que pasó**: al agregar `PasswordEncoder` como nueva dependencia de `ClienteService` (corrección de seguridad #1, contraseñas en texto plano), los tests `ClienteServiceTest` preexistentes (generados antes por `backend-test-generator`) fallaron con `NullPointerException` porque mockeaban el constructor anterior, sin conocer la nueva dependencia.

**Corrección**: se identificó la causa real (no se asumió que era un bug del código de producción) y se actualizó el test para mockear `PasswordEncoder` y ajustar las aserciones al nuevo flujo de hash. Mismo patrón se repitió con `ReporteControllerTest` tras mover el parsing de fechas al mapper (el mock del mapper no tenía estubeado el nuevo método `parsearRangoFechas`, causando `500` en el test).

**Por qué importa**: ilustra que cada corrección de IA se verificó ejecutando la suite de tests real después del cambio, no solo revisando el código modificado de forma aislada — varias regresiones se habrían colado sin esa verificación.

## 6. Documentación generada por IA que quedó desactualizada, corregida tras verificación cruzada

**Lo que pasó**: el agente `backend-readme-writer` generó el `README.md` verificando el código real, pero el comando de ejemplo para correr JMeter asumía `jmeter` disponible en el `PATH` del sistema (no lo está; se había descargado a una ruta temporal en una sesión anterior). Además, una sección de "trabajo futuro" del propio README, y por separado `backend/docs/progreso.md`, seguían afirmando que las pasadas formales de `backend-code-quality`/`backend-security-review` estaban "pendientes", cuando ya se habían completado en una etapa posterior del proyecto.

**Corrección**: se revisó el README línea por línea contra el estado real del proyecto (no se aceptó el primer borrador por defecto), se corrigió el comando de JMeter para reflejar que requiere descarga manual, y se reescribió la sección correspondiente para reflejar que la revisión formal ya está completa, con el detalle de los 10 hallazgos corregidos. `backend/docs/progreso.md` se reescribió completo por la misma razón.

## 7. Decisión consciente de NO corregir un hallazgo de seguridad

**Hallazgo del agente `backend-security-review`**: el puerto `8081` del microservicio `cliente` queda expuesto al host sin mecanismo de autenticación entre servicios, lo cual en un entorno de producción real sería un riesgo (acceso directo no autorizado).

**Decisión humana**: no se cerró el puerto. El PDF exige explícitamente poder validar `/clientes` con Postman/Swagger de forma directa — cerrar el puerto rompería ese requisito de evaluación. Se documentó la decisión y su justificación directamente en `docker-compose.yml` como comentario, en vez de aplicar una "corrección" que generaría un problema distinto (no poder cumplir el entregable de Postman).

## 8. Bug silencioso en `.gitignore` que excluyó código de producción del repositorio, detectado solo al desplegar

**Lo que pasó**: el primer despliegue real del microservicio `cliente` en Render falló en el build con `cannot find symbol: class ClienteRepositoryPort` — un error que nunca apareció en ninguna compilación local durante todo el desarrollo. La causa: `.gitignore` tenía la regla `out/` (agregada para ignorar la carpeta de build de IntelliJ), pero en sintaxis de Git esa regla sin barra inicial ignora **cualquier carpeta llamada `out` en cualquier nivel del repositorio** — y la arquitectura hexagonal usa literalmente `domain/port/out/` e `infrastructure/adapter/out/` como nombres de paquete reales en ambos microservicios.

**Por qué nunca se detectó antes**: los archivos existían en el disco local y los builds (`mvn compile`, `ng build`, los 48+57 tests) siempre se corrieron contra el sistema de archivos local, no contra lo que realmente había en Git. El `git add -A` inicial silenciosamente omitió 19 archivos de producción (entidades JPA, repositorios, adapters, mappers de ambos microservicios) sin ningún mensaje de error visible en el flujo normal de trabajo.

**Corrección**: se cambió la regla a `/out/` (ancla solo a la raíz del proyecto, donde sí vive la carpeta de IntelliJ), se identificaron los 19 archivos afectados comparando el disco contra `git ls-files`, se agregaron y commitearon, y se verificó con un build limpio (`mvn clean package`) idéntico al que ejecuta el `Dockerfile` antes de reintentar el deploy.

**Por qué importa**: es el ejemplo más claro de esta sesión de un error que **ningún test local podía detectar por diseño** (los tests corren contra el filesystem, no contra el repositorio remoto) — solo se hizo visible al intentar un despliegue real desde el código tal como queda en GitHub. Refuerza por qué vale la pena probar el pipeline de CI/CD real (build desde un checkout limpio del repo) antes de la entrega final, no solo confiar en que "compila en mi máquina".
