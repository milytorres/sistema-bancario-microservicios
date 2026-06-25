# ---- Stage 1: Build ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copiar primero el wrapper y el pom para aprovechar la cache de capas de Docker
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

# Copiar el código fuente y compilar el jar (sin tests, ya se validan en otra etapa del pipeline)
COPY src/ src/
RUN ./mvnw -B clean package -DskipTests

# ---- Stage 2: Runtime ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

# Variables de entorno soportadas (con defaults en application.properties para localhost).
# En produccion (Render) cada microservicio se despliega como un servicio Docker independiente
# y requiere configurar estas env vars:
#   SERVER_PORT          puerto HTTP del servicio (8081)
#   DB_HOST              host de PostgreSQL
#   DB_PORT              puerto de PostgreSQL (5432)
#   DB_NAME              nombre de la base de datos (banco_cliente)
#   DB_USER              usuario de PostgreSQL
#   DB_PASSWORD          password de PostgreSQL
#   JPA_DDL_AUTO         estrategia ddl de Hibernate (validate)

ENTRYPOINT ["java", "-jar", "app.jar"]
