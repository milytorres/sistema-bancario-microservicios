# Agente de apoyo al desarrollo — Sistema de 9 subagentes especializados

Esta es la evidencia de la **Sección 6.1, Opción 1** del PDF ("Agente / asistente de desarrollo: un agente que ayude a optimizar tareas repetitivas del proyecto"). No es un único agente genérico, sino una **flota de 9 subagentes especializados de Claude Code**, cada uno con un rol fijo y reglas propias, invocados repetidamente a lo largo de las 4 sesiones de desarrollo del backend.

## Qué es técnicamente

Cada archivo `.md` de esta carpeta es un **subagente personalizado de Claude Code** (formato estándar de la herramienta): un archivo Markdown con frontmatter YAML (`name`, `description`, `tools`, `model`) seguido de un system prompt que define el rol, el alcance y las reglas estrictas del agente. Claude Code los descubre automáticamente cuando viven en `.claude/agents/` en la raíz del proyecto (la copia "fuente" de estos mismos archivos está en `D:\Stalin\TRABAJO\PRUEBAS\.claude\agents\`; aquí en `ai/agent/` queda la copia de evidencia para la entrega).

## Los 9 agentes y su responsabilidad

| Agente | Responsabilidad |
|---|---|
| `backend-db-modeler` | Modelo de dominio puro, puertos/adaptadores de persistencia (hexagonal), entidades JPA, conexión PostgreSQL, `BaseDatos.sql` |
| `backend-task-optimizer` | Boilerplate repetitivo: DTOs, mappers, capas Controller/Service/Repository a partir de una entidad |
| `backend-test-generator` | Pruebas unitarias (F5) e integración (F6), configuración de cobertura JaCoCo |
| `backend-api-docs` | Configuración de Swagger/OpenAPI en código y generación de colecciones Postman desde el contrato real |
| `backend-code-quality` | Revisión de calidad de código (capas, SOLID, manejo de excepciones) — solo lectura |
| `backend-security-review` | Revisión de seguridad (OWASP, secretos, validaciones, CORS) — solo lectura |
| `backend-deployment` | Dockerfile de cada microservicio, integración a `docker-compose.yml`, preparación para Render |
| `backend-performance-test` | Diseño y ejecución de pruebas de carga JMeter sobre endpoints críticos |
| `backend-readme-writer` | Redacción del README del proyecto a partir del código y las decisiones reales |

## Cómo ejecutarlos (reproducibilidad)

**Requisito**: Claude Code (CLI o el entorno equivalente que use Claude con la herramienta `Agent`/subagentes), con este repositorio como directorio de trabajo.

1. Asegúrate de que los archivos vivan en `.claude/agents/` en la raíz del proyecto (no en `ai/agent/`, que es solo la copia de evidencia).
2. En una sesión de Claude Code dentro del repo, invoca el agente por su `name` (el campo del frontmatter), por ejemplo:
   > "Usa el agente `backend-test-generator` para generar las pruebas unitarias de la entidad `Cuenta`."
3. Claude Code (o el harness equivalente) detecta el subagente por `name`/`description` y ejecuta el prompt dentro del contexto y las reglas definidas en ese archivo, con acceso solo a las herramientas listadas en `tools:`.
4. El agente reporta su resultado; en esta sesión, cada resultado fue verificado de forma independiente (compilando, corriendo tests reales, o probando los endpoints con `curl`/`newman`) antes de aceptarlo — ver `../decisions.md`.

No requieren instalación ni configuración adicional: son texto plano interpretado por el propio asistente de IA, no un programa separado que haya que compilar o desplegar.
